package com.chessigma.app.engine

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveList

// Stub for EngineResult
data class EngineResult(
    val bestMoveUci: String,
    val scoreCp: Int
)

// UciParser
object UciParser {
    fun parseUciToSan(uci: String, fen: String): String {
        return try {
            val board = Board()
            board.loadFromFen(fen)
            val move = Move(uci, board.sideToMove)
            val moveList = MoveList(fen)
            moveList.add(move)
            moveList.toSan().trim()
        } catch (e: Exception) {
            uci
        }
    }
}
