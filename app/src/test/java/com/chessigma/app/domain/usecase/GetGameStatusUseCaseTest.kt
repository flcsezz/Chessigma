package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.GameStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class GetGameStatusUseCaseTest {

    private val getGameStatusUseCase = GetGameStatusUseCase()

    @Test
    fun `ongoing position returns ONGOING`() {
        val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        assertEquals(GameStatus.ONGOING, getGameStatusUseCase(fen))
    }

    @Test
    fun `checkmate position returns CHECKMATE`() {
        // Fools mate
        val fen = "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 0 3"
        assertEquals(GameStatus.CHECKMATE, getGameStatusUseCase(fen))
    }

    @Test
    fun `stalemate position returns STALEMATE`() {
        // Typical stalemate
        val fen = "k7/8/K7/8/8/8/8/1Q6 w - - 0 1" 
        // Wait, 1Q6 w - - 0 1 is white to move.
        // Stalemate is when side to move has no legal moves but is not in check.
        val stalemateFen = "5k2/5P2/5K2/8/8/8/8/8 b - - 0 1"
        assertEquals(GameStatus.STALEMATE, getGameStatusUseCase(stalemateFen))
    }
}
