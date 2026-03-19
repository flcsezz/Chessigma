package com.chessigma.app.domain.model

data class EloPoint(
    val platform: String,
    val date: Long,
    val elo: Int
)
