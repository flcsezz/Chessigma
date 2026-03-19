package com.chessigma.app.engine

import org.junit.Assert.assertEquals
import org.junit.Test

class UciParserTest {

    @Test
    fun testParseUciToSan_startingPosition() {
        val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        val result = UciParser.parseUciToSan("e2e4", fen)
        println("Result for e2e4: '$result'")
        assertEquals("e4", result)
    }

    @Test
    fun testParseUciToSan_capture() {
        val fen = "r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3"
        // White plays Nxe5
        assertEquals("Nxe5", UciParser.parseUciToSan("f3e5", fen))
    }

    @Test
    fun testParseUciToSan_promotion() {
        val fen = "8/4P3/8/8/8/8/8/k6K w - - 0 1"
        assertEquals("e8=Q", UciParser.parseUciToSan("e7e8q", fen))
    }
}
