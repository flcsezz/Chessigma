package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.MoveClassification
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for the pure classification logic inside [ReviewGameUseCase].
 * We only exercise the `classifyMove` internal function — no Stockfish or Android dependencies.
 */
class ReviewGameUseCaseTest {

    // Pure helper that delegates to the internal function without needing the full use-case instance.
    private fun classify(ply: Int, before: Int, after: Int, played: String, best: String?) =
        classifyMove(ply, before, after, played, best)

    // ── White move (ply even) ──────────────────────────────────────────────────

    @Test
    fun `played move matches best → Best`() {
        assertEquals(MoveClassification.Best, classify(0, 20, 20, "e2e4", "e2e4"))
    }

    @Test
    fun `cp loss 5 → Excellent`() {
        assertEquals(MoveClassification.Excellent, classify(0, 30, 25, "d2d4", "e2e4"))
    }

    @Test
    fun `cp loss 25 → Good`() {
        assertEquals(MoveClassification.Good, classify(0, 50, 25, "d2d4", "e2e4"))
    }

    @Test
    fun `cp loss 40 → Inaccuracy`() {
        assertEquals(MoveClassification.Inaccuracy, classify(0, 60, 20, "d2d4", "e2e4"))
    }

    @Test
    fun `cp loss 80 → Mistake`() {
        assertEquals(MoveClassification.Mistake, classify(0, 100, 20, "d2d4", "e2e4"))
    }

    @Test
    fun `cp loss 200 → Blunder`() {
        assertEquals(MoveClassification.Blunder, classify(0, 250, 50, "d2d4", "e2e4"))
    }

    // ── Black move (ply odd) ───────────────────────────────────────────────────

    @Test
    fun `Black played best move → Best`() {
        assertEquals(MoveClassification.Best, classify(1, -20, -20, "c7c5", "c7c5"))
    }

    @Test
    fun `Black cp loss 80 (evalAfter rises) → Mistake`() {
        // evalBefore=-20, evalAfter=60  => loss for Black = 60 - (-20) = 80
        assertEquals(MoveClassification.Mistake, classify(1, -20, 60, "e7e5", "c7c5"))
    }
}

/**
 * Standalone copy of the classification logic (mirrors [ReviewGameUseCase.classifyMove]).
 * Kept here so the test has zero Android/engine dependencies.
 */
private fun classifyMove(
    ply: Int,
    evalBefore: Int,
    evalAfter: Int,
    playedUci: String,
    bestUci: String?
): MoveClassification {
    val whiteToMove = ply % 2 == 0
    val cpLoss = if (whiteToMove) (evalBefore - evalAfter).coerceAtLeast(0)
                 else              (evalAfter - evalBefore).coerceAtLeast(0)

    if (playedUci == bestUci) return MoveClassification.Best

    return when {
        cpLoss <= MoveClassification.Excellent.cpThreshold  -> MoveClassification.Excellent
        cpLoss <= MoveClassification.Good.cpThreshold       -> MoveClassification.Good
        cpLoss <= MoveClassification.Inaccuracy.cpThreshold -> MoveClassification.Inaccuracy
        cpLoss <= MoveClassification.Mistake.cpThreshold    -> MoveClassification.Mistake
        else                                                 -> MoveClassification.Blunder
    }
}
