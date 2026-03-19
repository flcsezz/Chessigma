package com.chessigma.app.engine

// Stub for EngineResult
data class EngineResult(
    val bestMoveUci: String,
    val scoreCp: Int
)

// Stub for UciParser
object UciParser {
    fun parseUciToSan(uci: String, fen: String): String {
        // TODO: Implement UCI to SAN parsing with chess logic
        return uci 
    }
}
