package com.chessigma.app.domain.model

enum class PieceColor {
    WHITE, BLACK
}

enum class PieceType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

data class ChessPiece(
    val type: PieceType,
    val color: PieceColor
) {
    val symbol: String
        get() = when (type) {
            PieceType.PAWN -> "P"
            PieceType.KNIGHT -> "N"
            PieceType.BISHOP -> "B"
            PieceType.ROOK -> "R"
            PieceType.QUEEN -> "Q"
            PieceType.KING -> "K"
        }

    val unicodeSymbol: String
        get() = when (color) {
            PieceColor.WHITE -> when (type) {
                PieceType.PAWN -> "♙"
                PieceType.KNIGHT -> "♘"
                PieceType.BISHOP -> "♗"
                PieceType.ROOK -> "♖"
                PieceType.QUEEN -> "♕"
                PieceType.KING -> "♔"
            }
            PieceColor.BLACK -> when (type) {
                PieceType.PAWN -> "♟"
                PieceType.KNIGHT -> "♞"
                PieceType.BISHOP -> "♝"
                PieceType.ROOK -> "♜"
                PieceType.QUEEN -> "♛"
                PieceType.KING -> "♚"
            }
        }
}
