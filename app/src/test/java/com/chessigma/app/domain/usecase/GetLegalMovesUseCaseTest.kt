package com.chessigma.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetLegalMovesUseCaseTest {

    private val getLegalMovesUseCase = GetLegalMovesUseCase()

    @Test
    fun `when starting position, white pawn at e2 has legal moves e3 and e4`() {
        val startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        val legalMoves = getLegalMovesUseCase(startFen, "e2")
        
        assertEquals(2, legalMoves.size)
        assertTrue(legalMoves.contains("e3"))
        assertTrue(legalMoves.contains("e4"))
    }

    @Test
    fun `when starting position, white knight at g1 has legal moves f3 and h3`() {
        val startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        val legalMoves = getLegalMovesUseCase(startFen, "g1")
        
        assertEquals(2, legalMoves.size)
        assertTrue(legalMoves.contains("f3"))
        assertTrue(legalMoves.contains("h3"))
    }

    @Test
    fun `when king is in check, legal moves are restricted`() {
        // Scholar's mate attempt, black queen at f2 checking white king at e1
        val checkFen = "rnbqkbnr/ppppp1pp/8/5p2/4P3/8/PPPP1qPP/RNBQKBNR w KQkq - 0 1"
        val legalMoves = getLegalMovesUseCase(checkFen, "e1")
        
        // King must capture queen at f2
        assertEquals(1, legalMoves.size)
        assertEquals("f2", legalMoves[0])
    }
}
