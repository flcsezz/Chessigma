package com.chessigma.app.data.repository

import com.chessigma.app.data.local.CoachInsightDao
import com.chessigma.app.data.local.CoachInsightEntity
import com.chessigma.app.data.remote.api.GeminiApiService
import com.chessigma.app.data.remote.api.GroqApiService
import com.chessigma.app.data.remote.api.NvidiaApiService
import com.chessigma.app.data.remote.dto.*
import com.chessigma.app.domain.model.AiCascadeState
import com.chessigma.app.domain.model.CoachInsight
import com.chessigma.app.domain.model.MoveClassification
import com.chessigma.app.domain.model.ReviewMoveResult
import com.chessigma.app.domain.model.YoutubeSuggestion
import com.chessigma.app.domain.repository.LocalGameRepository
import com.chessigma.app.domain.usecase.GenerateReviewCoachInsightUseCase
import com.chessigma.app.util.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val localGameRepository: LocalGameRepository,
    private val coachInsightDao: CoachInsightDao,
    private val generateReviewCoachInsightUseCase: GenerateReviewCoachInsightUseCase,
    private val geminiApi: GeminiApiService,
    private val groqApi: GroqApiService,
    private val nvidiaApi: NvidiaApiService
) {
    private val _cascadeState = MutableStateFlow<AiCascadeState>(AiCascadeState.Idle)
    val cascadeState: StateFlow<AiCascadeState> = _cascadeState

    // Placeholder keys — in a real app, these would be in BuildConfig or DataStore
    private val GEMINI_KEY = "GEMINI_API_KEY_PLACEHOLDER"
    private val GROQ_KEY = "GROQ_API_KEY_PLACEHOLDER"
    private val NVIDIA_KEY = "NVIDIA_API_KEY_PLACEHOLDER"

    suspend fun generateCoachInsight() {
        val latestReviewedGame = localGameRepository
            .getRecentGames()
            .first()
            .firstOrNull { it.isAnalysed }
            ?: run {
                _cascadeState.value = AiCascadeState.Idle
                return
            }

        val (_, moveEntities) = localGameRepository.loadGame(latestReviewedGame.id) ?: run {
            _cascadeState.value = AiCascadeState.Idle
            return
        }

        val reviewedMoves = moveEntities.mapNotNull { entity ->
            val evalBefore = entity.evalCpBefore ?: return@mapNotNull null
            val evalAfter = entity.evalCpAfter ?: return@mapNotNull null
            val classification = MoveClassification.fromStorage(entity.classification) ?: return@mapNotNull null
            ReviewMoveResult(
                ply = entity.ply,
                san = entity.san,
                uci = entity.uci,
                fenBefore = entity.fenBefore,
                evalCpBefore = evalBefore,
                evalCpAfter = evalAfter,
                classification = classification,
                bestUci = entity.bestUci,
                bestSan = entity.bestSan
            )
        }

        if (reviewedMoves.isEmpty()) {
            _cascadeState.value = if (networkMonitor.isConnected()) {
                AiCascadeState.Idle
            } else {
                AiCascadeState.Offline
            }
            return
        }

        generateReviewCoachInsight(latestReviewedGame.id, reviewedMoves)
    }

    suspend fun generateReviewCoachInsight(
        gameId: String,
        moves: List<ReviewMoveResult>
    ) {
        val game = localGameRepository.loadGame(gameId)?.first
        
        // 1. Generate deterministic fallback/base insight
        val baseInsight = generateReviewCoachInsightUseCase(game, moves)

        if (!networkMonitor.isConnected()) {
            _cascadeState.value = AiCascadeState.Offline
            saveAndEmitSuccess(baseInsight, Json.encodeToString(baseInsight))
            return
        }

        val prompt = buildPrompt(game, moves, baseInsight)

        // 2. Start Cascade
        _cascadeState.value = AiCascadeState.Loading("Gemini 1.5")
        if (tryCallGemini(prompt)) return

        _cascadeState.value = AiCascadeState.Loading("Groq (Mixtral)")
        if (tryCallGroq(prompt)) return

        _cascadeState.value = AiCascadeState.Loading("NVIDIA NIM (Llama-3)")
        if (tryCallNvidia(prompt)) return

        // 3. All failed — emitter base insight
        _cascadeState.value = AiCascadeState.AllProvidersFailed
        saveAndEmitSuccess(baseInsight, Json.encodeToString(baseInsight))
    }

    private fun buildPrompt(
        game: com.chessigma.app.data.local.GameEntity?,
        moves: List<ReviewMoveResult>,
        base: CoachInsight
    ): String {
        return """
            You are a master chess coach. Analyse this game for the ${game?.userColor ?: "player"}.
            Game Result: ${game?.result ?: "Unknown"}
            Base Summary: ${base.summaryText}
            Strengths Identified: ${base.strengths.joinToString()}
            Weaknesses Identified: ${base.weaknesses.joinToString()}
            
            Key Moves Data:
            ${moves.filter { it.classification != MoveClassification.Good && it.classification != MoveClassification.Best }
                .take(10)
                .joinToString("\n") { 
                    "Ply ${it.ply}: ${it.san} was ${it.classification}. Eval dropped from ${it.evalCpBefore} to ${it.evalCpAfter}." 
                }
            }
            
            Return a JSON object matching this schema:
            {
              "weaknesses": ["string"],
              "strengths": ["string"],
              "youtube_suggestions": [{"title": "string", "channel": "string", "search_query": "string"}],
              "one_line_verdict": "string"
            }
            The "one_line_verdict" should be a professional, encouraging, and highly specific summary of the game.
        """.trimIndent()
    }

    private suspend fun tryCallGemini(prompt: String): Boolean {
        return try {
            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(prompt))))
            )
            val response = geminiApi.generateContent(GEMINI_KEY, request)
            val jsonText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return false
            parseAndSaveAiResponse(jsonText)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun tryCallGroq(prompt: String): Boolean {
        return try {
            val request = ChatCompletionRequest(
                model = "mixtral-8x7b-32768",
                messages = listOf(ChatMessage("user", prompt))
            )
            val response = groqApi.createChatCompletion("Bearer $GROQ_KEY", request)
            val jsonText = response.choices.firstOrNull()?.message?.content ?: return false
            parseAndSaveAiResponse(jsonText)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun tryCallNvidia(prompt: String): Boolean {
        return try {
            val request = ChatCompletionRequest(
                model = "meta/llama3-70b-instruct",
                messages = listOf(ChatMessage("user", prompt))
            )
            val response = nvidiaApi.createChatCompletion("Bearer $NVIDIA_KEY", request)
            val jsonText = response.choices.firstOrNull()?.message?.content ?: return false
            parseAndSaveAiResponse(jsonText)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun parseAndSaveAiResponse(jsonText: String) {
        // Clean JSON formatting if AI wrapped it in markdown
        val cleaned = jsonText.trim().removePrefix("```json").removeSuffix("```").trim()
        val dto = Json.decodeFromString<com.chessigma.app.data.remote.dto.CoachInsightDto>(cleaned)
        
        val insight = CoachInsight(
            id = 0,
            weaknesses = dto.weaknesses,
            strengths = dto.strengths,
            youtubeLinks = dto.youtube_suggestions.map { YoutubeSuggestion(it.title, it.channel, it.search_query) },
            summaryText = dto.one_line_verdict
        )
        saveAndEmitSuccess(insight, cleaned)
    }

    private suspend fun saveAndEmitSuccess(
        insight: CoachInsight,
        rawJson: String
    ) {
        val entity = CoachInsightEntity(
            generatedAt = System.currentTimeMillis(),
            gamesAnalysed = 1,
            weaknessesJson = Json.encodeToString(insight.weaknesses),
            strengthsJson = Json.encodeToString(insight.strengths),
            youtubeLinksJson = Json.encodeToString(insight.youtubeLinks),
            summaryText = insight.summaryText,
            rawApiResponse = rawJson
        )
        val persistedId = coachInsightDao.insertInsight(entity)

        val domainInsight = insight.copy(
            id = persistedId
        )
        _cascadeState.value = AiCascadeState.Success(domainInsight)
    }
}
