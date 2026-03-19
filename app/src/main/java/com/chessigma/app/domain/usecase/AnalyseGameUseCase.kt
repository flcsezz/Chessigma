package com.chessigma.app.domain.usecase

import com.chessigma.app.ui.puzzles.PuzzleGenerator
import javax.inject.Inject

class AnalyseGameUseCase @Inject constructor(
    private val puzzleGenerator: PuzzleGenerator
    // inject StockfishEngine wrapper here eventually
) {
    suspend operator fun invoke(gameId: String) {
        // TODO: Stockfish annotates every move of a game
        
        // After Stockfish finishes annotating:
        puzzleGenerator.generateFromGame(gameId)
    }
}
