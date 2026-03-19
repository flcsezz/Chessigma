package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.repository.EngineRepository
import javax.inject.Inject

class AnalyzePositionUseCase @Inject constructor(
    private val engineRepository: EngineRepository
) {
    suspend operator fun invoke(fen: String, depth: Int = 12): AnalysisResult {
        val bestMove = engineRepository.getBestMove(fen, depth)
        val evaluation = engineRepository.evaluatePosition(fen, depth)
        return AnalysisResult(bestMove, evaluation)
    }

    data class AnalysisResult(
        val bestMove: String,
        val evaluation: Int // Centipawns
    )
}
