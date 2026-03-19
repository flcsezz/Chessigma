package com.chessigma.app.domain.model

data class ChessMove(
    val fromSquare: String,
    val toSquare: String,
    val promotionPiece: PieceType? = null,
    val san: String? = null // Standard Algebraic Notation
) {
    fun toUci(): String {
        val base = fromSquare + toSquare
        return if (promotionPiece != null) {
            base + promotionPiece.name.first().lowercaseChar()
        } else {
            base
        }
    }
}
