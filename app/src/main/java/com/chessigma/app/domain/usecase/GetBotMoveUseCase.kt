package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.ChessMove
import com.chessigma.app.domain.repository.EngineRepository
import com.chessigma.app.engine.UciParser
import javax.inject.Inject

class GetBotMoveUseCase @Inject constructor(
    private val engineRepository: EngineRepository
) {
    /**
     * Get the best move from the engine for a given FEN and skill level.
     * @param fen Current board position.
     * @param level Stockfish Skill Level (0-20).
     * @param depth Search depth (defaults to 10 for responsive bot play).
     */
    suspend operator fun invoke(
        fen: String,
        level: Int,
        depth: Int = 10
    ): ChessMove? {
        engineRepository.setSkillLevel(level)
        val uciMove = engineRepository.getBestMove(fen, depth)
        if (uciMove.isEmpty()) return null
        
        // Parse UCI to find promotion piece or just squares
        val fromSquare = uciMove.substring(0, 2)
        val toSquare = uciMove.substring(2, 4)
        val san = UciParser.parseUciToSan(uciMove, fen)
        
        return ChessMove(
            fromSquare = fromSquare,
            toSquare = toSquare,
            san = san
        )
    }
}
