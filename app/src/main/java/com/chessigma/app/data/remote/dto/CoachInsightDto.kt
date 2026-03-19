package com.chessigma.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoachInsightDto(
    val weaknesses: List<String>,
    val strengths: List<String>,
    val youtube_suggestions: List<YoutubeSuggestionDto>,
    val one_line_verdict: String
)

@Serializable
data class YoutubeSuggestionDto(
    val title: String,
    val channel: String,
    val search_query: String
)
