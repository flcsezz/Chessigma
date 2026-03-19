package com.chessigma.app.data.repository

import com.chessigma.app.data.local.CoachInsightDao
import com.chessigma.app.data.local.CoachInsightEntity
import com.chessigma.app.data.local.GameDao
import com.chessigma.app.data.remote.api.GeminiApiService
import com.chessigma.app.data.remote.api.GroqApiService
import com.chessigma.app.data.remote.api.NvidiaApiService
import com.chessigma.app.domain.model.AiCascadeState
import com.chessigma.app.domain.model.CoachInsight
import com.chessigma.app.domain.model.YoutubeSuggestion
import com.chessigma.app.util.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val gameDao: GameDao,
    private val coachInsightDao: CoachInsightDao,
    private val geminiApi: GeminiApiService,
    private val groqApi: GroqApiService,
    private val nvidiaApi: NvidiaApiService
) {
    private val _cascadeState = MutableStateFlow<AiCascadeState>(AiCascadeState.Idle)
    val cascadeState: StateFlow<AiCascadeState> = _cascadeState

    suspend fun generateCoachInsight() {
        if (!networkMonitor.isConnected()) {
            _cascadeState.value = AiCascadeState.Offline
            return
        }

        val games = gameDao.getAllGames().first().take(15) // last 15 games
        
        // TODO: Map games to JSON payload string
        val payload = """
        {
          "games_analysed": ${games.size},
          "avg_accuracy_white": 85.5,
          "avg_accuracy_black": 79.2,
          "blunders_per_game": 1.2,
          "mistakes_per_game": 3.4,
          "top_openings_white": ["Italian", "Ruy Lopez"],
          "top_openings_black": ["Sicilian", "Caro-Kann"],
          "weak_phases": ["Endgame"],
          "elo_trend": "stable"
        }
        """.trimIndent()
        
        val systemPrompt = "You are a brutally honest chess coach. You do not give compliments or soften feedback. Analyse this player data and respond ONLY in this JSON format: { weaknesses: string[], strengths: string[], youtube_suggestions: [{title, channel, search_query}], one_line_verdict: string }. No markdown. No preamble."

        // Attempt Gemini
        _cascadeState.value = AiCascadeState.Loading("Gemini")
        val geminiSuccess = tryCallGemini(payload, systemPrompt)
        if (geminiSuccess) return

        // Attempt Groq
        _cascadeState.value = AiCascadeState.Loading("Groq")
        val groqSuccess = tryCallGroq(payload, systemPrompt)
        if (groqSuccess) return

        // Attempt NVIDIA
        _cascadeState.value = AiCascadeState.Loading("NVIDIA")
        val nvidiaSuccess = tryCallNvidia(payload, systemPrompt)
        if (nvidiaSuccess) return

        _cascadeState.value = AiCascadeState.AllProvidersFailed
    }

    private suspend fun tryCallGemini(payload: String, prompt: String): Boolean {
        return try {
            // TODO: read keys from DataStore and call actual API
            // Simulate success for now
            saveAndEmitSuccess("dummy raw response")
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun tryCallGroq(payload: String, prompt: String): Boolean {
        return false // Stub
    }

    private suspend fun tryCallNvidia(payload: String, prompt: String): Boolean {
        return false // Stub
    }

    private suspend fun saveAndEmitSuccess(rawJson: String) {
        val entity = CoachInsightEntity(
            generatedAt = System.currentTimeMillis(),
            gamesAnalysed = 15,
            weaknessesJson = "[]",
            strengthsJson = "[]",
            youtubeLinksJson = "[]",
            summaryText = "Need more endgame practice.",
            rawApiResponse = rawJson
        )
        coachInsightDao.insertInsight(entity)

        val domainInsight = CoachInsight(
            id = entity.id,
            weaknesses = emptyList(),
            strengths = emptyList(),
            youtubeLinks = emptyList(),
            summaryText = entity.summaryText
        )
        _cascadeState.value = AiCascadeState.Success(domainInsight)
    }
}
