package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.PieceColor
import com.chessigma.app.domain.model.PieceType
import org.junit.Assert.*
import org.junit.Test

class ParseFenUseCaseTest {
    
    private val parseFenUseCase = ParseFenUseCase()
    
    @Test
    fun `parse initial position`() {
        val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        val board = parseFenUseCase(fen)
        
        assertEquals(32, board.pieces.size)
        assertEquals(PieceColor.WHITE, board.sideToMove)
        assertEquals(PieceType.ROOK, board.pieces["a1"]?.type)
        assertEquals(PieceColor.WHITE, board.pieces["a1"]?.color)
        assertEquals(PieceType.KING, board.pieces["e8"]?.type)
        assertEquals(PieceColor.BLACK, board.pieces["e8"]?.color)
        assertFalse(board.isCheck)
        assertFalse(board.isCheckmate)
    }
    
    @Test
    fun `parse check position`() {
        // Position where black king is in check by white queen
        val fen = "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 0 1"
        val board = parseFenUseCase(fen)
        
        assertTrue(board.isCheck)
        assertTrue(board.isCheckmate) // Fool's mate
    }
}
