package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.ChessMove
import org.junit.Assert.*
import org.junit.Test

class ValidateMoveUseCaseTest {
    
    private val validateMoveUseCase = ValidateMoveUseCase()
    
    @Test
    fun `validate legal move`() {
        val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        val move = ChessMove("e2", "e4")
        
        assertTrue(validateMoveUseCase(fen, move))
    }
    
    @Test
    fun `validate illegal move`() {
        val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        val move = ChessMove("e2", "e5") // Pawn moves 3 squares
        
        assertFalse(validateMoveUseCase(fen, move))
    }
    
    @Test
    fun `validate castling`() {
        val fen = "rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 1"
        val kingsideCastle = ChessMove("e1", "g1")
        
        assertTrue(validateMoveUseCase(fen, kingsideCastle))
    }
}
