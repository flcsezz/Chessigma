package com.chessigma.app.domain.model

data class PersonalPuzzle(
    val id: Long,
    val sourceGameId: String,
    val ply: Int,
    val fenPosition: String,
    val correctUci: String,
    val correctSan: String,
    val blunderSan: String,
    val originalClassification: String,
    val solved: Boolean,
    val solvedAt: Long?,
    val attemptCount: Int,
    val createdAt: Long
) {
    val isFromBlunder: Boolean
        get() = originalClassification == "BLUNDER"
}
