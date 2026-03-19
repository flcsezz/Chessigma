package com.chessigma.app.domain.model

data class ChessBoard(
    val pieces: Map<String, ChessPiece>, // Key is square name like "a1", "e4"
    val sideToMove: PieceColor,
    val isCheck: Boolean,
    val isCheckmate: Boolean,
    val isDraw: Boolean,
    val fen: String
) {
    companion object {
        fun empty() = ChessBoard(
            pieces = emptyMap(),
            sideToMove = PieceColor.WHITE,
            isCheck = false,
            isCheckmate = false,
            isDraw = false,
            fen = "8/8/8/8/8/8/8/8 w - - 0 1"
        )
    }
}
