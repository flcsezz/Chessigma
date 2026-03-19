package com.chessigma.app.data.repository

import com.chessigma.app.data.local.CoachInsightDao
import com.chessigma.app.data.local.CoachInsightEntity
import com.chessigma.app.data.remote.api.GeminiApiService
import com.chessigma.app.data.remote.api.GroqApiService
import com.chessigma.app.data.remote.api.NvidiaApiService
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
        _cascadeState.value = AiCascadeState.Loading("Review Summary")

        val game = localGameRepository.loadGame(gameId)?.first
        val insight = generateReviewCoachInsightUseCase(game, moves)
        saveAndEmitSuccess(
            insight = insight,
            rawJson = Json.encodeToString(insight)
        )
    }

    @Suppress("unused")
    private suspend fun tryCallGemini(payload: String, prompt: String): Boolean {
        return false
    }

    @Suppress("unused")
    private suspend fun tryCallGroq(payload: String, prompt: String): Boolean {
        return false
    }

    @Suppress("unused")
    private suspend fun tryCallNvidia(payload: String, prompt: String): Boolean {
        return false
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
