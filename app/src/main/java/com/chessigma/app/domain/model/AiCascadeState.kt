package com.chessigma.app.domain.model

import kotlinx.serialization.Serializable

sealed class AiCascadeState {
    object Idle : AiCascadeState()
    data class Loading(val provider: String) : AiCascadeState()
    data class Success(val insight: CoachInsight) : AiCascadeState()
    data class RateLimited(val provider: String) : AiCascadeState()
    object AllProvidersFailed : AiCascadeState()
    object Offline : AiCascadeState()
}

@Serializable
data class CoachInsight(
    val id: Long,
    val weaknesses: List<String>,
    val strengths: List<String>,
    val youtubeLinks: List<YoutubeSuggestion>,
    val summaryText: String
)

@Serializable
data class YoutubeSuggestion(
    val title: String,
    val channel: String,
    val searchQuery: String
)
