package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.*
import com.github.bhlangon.chesslib.Board
import com.github.bhlangon.chesslib.Square
import com.github.bhlangon.chesslib.move.Move
import javax.inject.Inject

class ValidateMoveUseCase @Inject constructor() {
    
    operator fun invoke(fen: String, move: ChessMove): Boolean {
        val board = Board()
        board.loadFromFen(fen)
        
        val fromSquare = Square.fromValue(move.fromSquare.uppercase())
        val toSquare = Square.fromValue(move.toSquare.uppercase())
        
        val libMove = if (move.promotionPiece != null) {
            val promotion = mapPromotionPiece(move.promotionPiece, board.sideToMove)
            Move(fromSquare, toSquare, promotion)
        } else {
            Move(fromSquare, toSquare)
        }
        
        return board.legalMoves().contains(libMove)
    }
    
    private fun mapPromotionPiece(
        type: PieceType, 
        side: com.github.bhlangon.chesslib.Side
    ): com.github.bhlangon.chesslib.Piece {
        return when (type) {
            PieceType.QUEEN -> if (side == com.github.bhlangon.chesslib.Side.WHITE) 
                com.github.bhlangon.chesslib.Piece.WHITE_QUEEN else com.github.bhlangon.chesslib.Piece.BLACK_QUEEN
            PieceType.ROOK -> if (side == com.github.bhlangon.chesslib.Side.WHITE) 
                com.github.bhlangon.chesslib.Piece.WHITE_ROOK else com.github.bhlangon.chesslib.Piece.BLACK_ROOK
            PieceType.BISHOP -> if (side == com.github.bhlangon.chesslib.Side.WHITE) 
                com.github.bhlangon.chesslib.Piece.WHITE_BISHOP else com.github.bhlangon.chesslib.Piece.BLACK_BISHOP
            PieceType.KNIGHT -> if (side == com.github.bhlangon.chesslib.Side.WHITE) 
                com.github.bhlangon.chesslib.Piece.WHITE_KNIGHT else com.github.bhlangon.chesslib.Piece.BLACK_KNIGHT
            else -> com.github.bhlangon.chesslib.Piece.NONE
        }
    }
}
