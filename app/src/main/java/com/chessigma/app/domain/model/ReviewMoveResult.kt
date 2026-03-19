package com.chessigma.app.domain.model

/**
 * Per-move result produced by the review pipeline.
 * All evals are centipawns from the perspective of White (positive = White advantage).
 */
data class ReviewMoveResult(
    val ply: Int,
    val san: String,
    val uci: String,
    val fenBefore: String,
    val evalCpBefore: Int,
    val evalCpAfter: Int,
    val classification: MoveClassification,
    val bestUci: String?,
    val bestSan: String?
) {
    /** Centipawn loss from the side that played the move (always ≥ 0). */
    val cpLoss: Int
        get() {
            // ply 0-based: even ply = White played, odd ply = Black played
            val whiteToMove = ply % 2 == 0
            return if (whiteToMove) {
                (evalCpBefore - evalCpAfter).coerceAtLeast(0)
            } else {
                (evalCpAfter - evalCpBefore).coerceAtLeast(0)
            }
        }
}
