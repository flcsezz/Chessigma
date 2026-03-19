package com.chessigma.app.domain.model

sealed class AiCascadeState {
    object Idle : AiCascadeState()
    data class Loading(val provider: String) : AiCascadeState()
    data class Success(val insight: CoachInsight) : AiCascadeState() // Dummy CoachInsight since it was in domain
    data class RateLimited(val provider: String) : AiCascadeState()
    object AllProvidersFailed : AiCascadeState()
    object Offline : AiCascadeState()
}

// Stub for CoachInsight domain model corresponding to CoachInsightEntity
data class CoachInsight(
    val id: Long,
    val weaknesses: List<String>,
    val strengths: List<String>,
    val youtubeLinks: List<YoutubeSuggestion>,
    val summaryText: String
)

data class YoutubeSuggestion(
    val title: String,
    val channel: String,
    val searchQuery: String
)
