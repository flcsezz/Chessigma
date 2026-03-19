package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.ChessMove
import com.chessigma.app.domain.model.PieceType
import com.chessigma.app.domain.model.GameState
import com.chessigma.app.domain.model.GameStatus
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import javax.inject.Inject

import com.github.bhlangonijr.chesslib.move.MoveList

class ApplyMoveUseCase @Inject constructor(
    private val parseFenUseCase: ParseFenUseCase,
    private val getGameStatusUseCase: GetGameStatusUseCase
) {

    operator fun invoke(
        currentGameState: GameState,
        move: ChessMove
    ): GameState? {
        val board = Board()
        board.loadFromFen(currentGameState.board.fen)

        val fromSquare = Square.fromValue(move.fromSquare.uppercase())
        val toSquare = Square.fromValue(move.toSquare.uppercase())
        
        // If promotion piece is missing but required, we can't apply the move yet
        if (isPromotion(board, fromSquare, toSquare) && move.promotionPiece == null) {
            return null
        }

        val libMove = if (move.promotionPiece != null) {
            Move(fromSquare, toSquare, mapPromotionPiece(move.promotionPiece, board.sideToMove))
        } else {
            Move(fromSquare, toSquare)
        }

        if (!board.legalMoves().contains(libMove)) {
            return null
        }

        // Calculate SAN before applying the move to the board? 
        // No, MoveList needs the starting position and the move.
        val moveList = MoveList(currentGameState.board.fen)
        moveList.add(libMove)
        val san = try {
            moveList.toSan()
        } catch (e: Exception) {
            null
        }

        board.doMove(libMove)
        
        val newFen = board.fen
        val newBoard = parseFenUseCase(newFen)
        val newStatus = getGameStatusUseCase(newFen)

        val moveWithSan = move.copy(san = san)

        return GameState(
            board = newBoard,
            moveHistory = currentGameState.moveHistory + moveWithSan,
            fenHistory = currentGameState.fenHistory + currentGameState.board.fen,
            status = newStatus
        )
    }

    private fun isPromotion(board: Board, from: Square, to: Square): Boolean {
        val piece = board.getPiece(from)
        if (piece.pieceType != com.github.bhlangonijr.chesslib.PieceType.PAWN) return false
        val rank = to.value().last()
        return (piece.pieceSide == Side.WHITE && rank == '8') ||
               (piece.pieceSide == Side.BLACK && rank == '1')
    }

    private fun mapPromotionPiece(type: PieceType, side: Side): Piece {
        return when (type) {
            PieceType.QUEEN -> if (side == Side.WHITE) Piece.WHITE_QUEEN else Piece.BLACK_QUEEN
            PieceType.ROOK -> if (side == Side.WHITE) Piece.WHITE_ROOK else Piece.BLACK_ROOK
            PieceType.BISHOP -> if (side == Side.WHITE) Piece.WHITE_BISHOP else Piece.BLACK_BISHOP
            PieceType.KNIGHT -> if (side == Side.WHITE) Piece.WHITE_KNIGHT else Piece.BLACK_KNIGHT
            else -> Piece.NONE
        }
    }
}
