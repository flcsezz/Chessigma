package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.MoveClassification
import com.chessigma.app.domain.model.ReviewMoveResult
import javax.inject.Inject

class CalculateGameAccuracyUseCase @Inject constructor() {

    operator fun invoke(moves: List<ReviewMoveResult>, userColor: String?): Pair<Float, Float> {
        if (moves.isEmpty()) return Pair(0f, 0f)

        val whiteMoves = moves.filter { it.ply % 2 == 0 }
        val blackMoves = moves.filter { it.ply % 2 == 1 }

        val whiteAccuracy = if (whiteMoves.isNotEmpty()) calculateAccuracy(whiteMoves) else 0f
        val blackAccuracy = if (blackMoves.isNotEmpty()) calculateAccuracy(blackMoves) else 0f

        return when (userColor?.uppercase()) {
            "WHITE" -> Pair(whiteAccuracy, blackAccuracy)
            "BLACK" -> Pair(blackAccuracy, whiteAccuracy)
            else -> Pair(whiteAccuracy, blackAccuracy)
        }
    }

    private fun calculateAccuracy(moves: List<ReviewMoveResult>): Float {
        if (moves.isEmpty()) return 0f

        val totalMistakeScore: Int = moves.sumOf { move ->
            val score: Int = when (move.classification) {
                MoveClassification.Brilliant -> 0
                MoveClassification.Best -> 0
                MoveClassification.Excellent -> 0
                MoveClassification.Good -> 1
                MoveClassification.Inaccuracy -> 2
                MoveClassification.Mistake -> 4
                MoveClassification.Blunder -> 8
                MoveClassification.Miss -> 10
            }
            score
        }

        val averageMistakeScore = totalMistakeScore.toFloat() / moves.size
        return (100f - (averageMistakeScore * 10f)).coerceIn(0f, 100f)
    }

    fun getAccuracyColor(accuracy: Float): String {
        return when {
            accuracy >= 90 -> "#4A9B6F"
            accuracy >= 70 -> "#21AFDB"
            accuracy >= 50 -> "#E6A817"
            else -> "#D9534F"
        }
    }
}
