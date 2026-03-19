package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ApplyMoveUseCaseTest {

    private val parseFenUseCase = ParseFenUseCase()
    private val getGameStatusUseCase = GetGameStatusUseCase()
    private val applyMoveUseCase = ApplyMoveUseCase(parseFenUseCase, getGameStatusUseCase)

    @Test
    fun `applies legal move and returns updated game state`() {
        val initialBoard = parseFenUseCase("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        val initialState = GameState(board = initialBoard)
        
        val result = applyMoveUseCase(
            currentGameState = initialState,
            move = ChessMove(fromSquare = "e2", toSquare = "e4")
        )

        assertNotNull(result)
        assertEquals(PieceColor.BLACK, result!!.board.sideToMove)
        assertEquals(1, result.moveHistory.size)
        assertEquals("e2", result.moveHistory.first().fromSquare)
        assertEquals("e4", result.moveHistory.first().toSquare)
        assertEquals(1, result.fenHistory.size)
    }

    @Test
    fun `returns null for illegal move`() {
        val initialBoard = parseFenUseCase("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        val initialState = GameState(board = initialBoard)

        val result = applyMoveUseCase(
            currentGameState = initialState,
            move = ChessMove(fromSquare = "e2", toSquare = "e5")
        )

        assertNull(result)
    }

    @Test
    fun `returns null if promotion is required but not provided`() {
        val initialBoard = parseFenUseCase("6k1/4P3/8/8/8/8/8/6K1 w - - 0 1")
        val initialState = GameState(board = initialBoard)

        val result = applyMoveUseCase(
            currentGameState = initialState,
            move = ChessMove(fromSquare = "e7", toSquare = "e8")
        )

        assertNull(result)
    }

    @Test
    fun `applies promotion when provided`() {
        val initialBoard = parseFenUseCase("6k1/4P3/8/8/8/8/8/6K1 w - - 0 1")
        val initialState = GameState(board = initialBoard)

        val result = applyMoveUseCase(
            currentGameState = initialState,
            move = ChessMove(fromSquare = "e7", toSquare = "e8", promotionPiece = PieceType.QUEEN)
        )

        assertNotNull(result)
        assertEquals(PieceType.QUEEN, result!!.board.getPiece("e8")?.type)
    }
}
