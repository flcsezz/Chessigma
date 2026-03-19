package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.*
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Square
import javax.inject.Inject

class ParseFenUseCase @Inject constructor() {
    
    operator fun invoke(fen: String): ChessBoard {
        val board = Board()
        board.loadFromFen(fen)
        
        val piecesMap = mutableMapOf<String, ChessPiece>()
        for (square in Square.entries) {
            val piece = board.getPiece(square)
            if (piece != com.github.bhlangonijr.chesslib.Piece.NONE) {
                piecesMap[square.value().lowercase()] = ChessPiece(
                    type = mapPieceType(piece.pieceType),
                    color = mapPieceColor(piece.pieceSide)
                )
            }
        }
        
        return ChessBoard(
            pieces = piecesMap,
            sideToMove = mapPieceColor(board.sideToMove),
            isCheck = board.isKingAttacked,
            isCheckmate = board.isMated,
            isDraw = board.isDraw,
            fen = board.fen
        )
    }
    
    private fun mapPieceType(type: com.github.bhlangonijr.chesslib.PieceType): PieceType =
        when (type) {
            com.github.bhlangonijr.chesslib.PieceType.PAWN -> PieceType.PAWN
            com.github.bhlangonijr.chesslib.PieceType.KNIGHT -> PieceType.KNIGHT
            com.github.bhlangonijr.chesslib.PieceType.BISHOP -> PieceType.BISHOP
            com.github.bhlangonijr.chesslib.PieceType.ROOK -> PieceType.ROOK
            com.github.bhlangonijr.chesslib.PieceType.QUEEN -> PieceType.QUEEN
            com.github.bhlangonijr.chesslib.PieceType.KING -> PieceType.KING
            else -> PieceType.PAWN // Should not happen for valid pieces
        }
        
    private fun mapPieceColor(side: com.github.bhlangonijr.chesslib.Side): PieceColor =
        when (side) {
            com.github.bhlangonijr.chesslib.Side.WHITE -> PieceColor.WHITE
            com.github.bhlangonijr.chesslib.Side.BLACK -> PieceColor.BLACK
        }
}
