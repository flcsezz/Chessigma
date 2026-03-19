package com.chessigma.app.domain.usecase

import com.chessigma.app.data.local.GameEntity
import com.chessigma.app.domain.model.MoveClassification
import com.chessigma.app.domain.model.ReviewMoveResult
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateReviewCoachInsightUseCaseTest {

    private val useCase = GenerateReviewCoachInsightUseCase()

    @Test
    fun `focuses coaching on user color`() {
        val game = gameEntity(userColor = "BLACK")
        val moves = listOf(
            reviewMove(
                ply = 0,
                san = "e4",
                evalBefore = 0,
                evalAfter = -15,
                classification = MoveClassification.Excellent
            ),
            reviewMove(
                ply = 1,
                san = "c5",
                evalBefore = -15,
                evalAfter = 180,
                classification = MoveClassification.Blunder,
                bestUci = "e7e5"
            )
        )

        val insight = useCase(game, moves)

        assertTrue(insight.summaryText.contains("opening"))
        assertTrue(insight.weaknesses.any { it.contains("1 blunder") })
    }

    @Test
    fun `produces practice suggestions from recurring mistakes`() {
        val game = gameEntity(userColor = "WHITE")
        val moves = listOf(
            reviewMove(0, "e4", 0, -120, MoveClassification.Blunder, bestUci = "d2d4"),
            reviewMove(2, "Nf3", -40, -110, MoveClassification.Mistake, bestUci = "f1c4"),
            reviewMove(4, "Bc4", -70, -95, MoveClassification.Inaccuracy, bestUci = "d2d3"),
            reviewMove(6, "O-O", -80, -90, MoveClassification.Good, bestUci = "e1g1")
        )

        val insight = useCase(game, moves)

        assertTrue(insight.summaryText.contains("blunders"))
        assertTrue(insight.youtubeLinks.isNotEmpty())
        assertTrue(insight.youtubeLinks.any { it.searchQuery.contains("blunder") || it.searchQuery.contains("opening") })
    }

    private fun gameEntity(userColor: String) = GameEntity(
        id = "game-1",
        platform = "LOCAL",
        pgn = "",
        whiteName = "Player",
        blackName = "Opponent",
        whiteElo = null,
        blackElo = null,
        result = "0-1",
        datePlayed = 0L,
        openingEco = null,
        openingName = null,
        accuracyWhite = null,
        accuracyBlack = null,
        isAnalysed = true,
        userColor = userColor
    )

    private fun reviewMove(
        ply: Int,
        san: String,
        evalBefore: Int,
        evalAfter: Int,
        classification: MoveClassification,
        bestUci: String? = null
    ) = ReviewMoveResult(
        ply = ply,
        san = san,
        uci = "e2e4",
        fenBefore = "stub-fen",
        evalCpBefore = evalBefore,
        evalCpAfter = evalAfter,
        classification = classification,
        bestUci = bestUci,
        bestSan = null
    )
}
