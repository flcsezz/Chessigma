package com.chessigma.app.domain.usecase

import com.chessigma.app.data.local.GameEntity
import com.chessigma.app.domain.model.CoachInsight
import com.chessigma.app.domain.model.MoveClassification
import com.chessigma.app.domain.model.ReviewMoveResult
import com.chessigma.app.domain.model.YoutubeSuggestion
import javax.inject.Inject

class GenerateReviewCoachInsightUseCase @Inject constructor() {

    operator fun invoke(
        game: GameEntity?,
        moves: List<ReviewMoveResult>
    ): CoachInsight {
        if (moves.isEmpty()) {
            return CoachInsight(
                id = 0,
                weaknesses = listOf("No reviewed moves are available yet."),
                strengths = emptyList(),
                youtubeLinks = emptyList(),
                summaryText = "Finish a reviewed game before asking for coaching."
            )
        }

        val whiteMoves = moves.filter { it.ply % 2 == 0 }
        val blackMoves = moves.filter { it.ply % 2 == 1 }
        val focusMoves = when (game?.userColor?.uppercase()) {
            "BLACK" -> blackMoves
            "WHITE" -> whiteMoves
            else -> if (whiteMoves.size >= blackMoves.size) whiteMoves else blackMoves
        }.ifEmpty { moves }

        val bestMoves = focusMoves.count { it.classification == MoveClassification.Best }
        val stableMoves = focusMoves.count {
            it.classification in setOf(
                MoveClassification.Best,
                MoveClassification.Brilliant,
                MoveClassification.Excellent,
                MoveClassification.Good
            )
        }
        val inaccuracies = focusMoves.count { it.classification == MoveClassification.Inaccuracy }
        val mistakes = focusMoves.count { it.classification == MoveClassification.Mistake }
        val blunders = focusMoves.count { it.classification == MoveClassification.Blunder }
        val severeMoves = focusMoves.filter {
            it.classification == MoveClassification.Mistake || it.classification == MoveClassification.Blunder
        }
        val missedBest = focusMoves.count { it.bestUci != null && it.uci != it.bestUci }
        val biggestMiss = severeMoves.maxByOrNull { it.cpLoss }

        val openingDamage = phaseScore(focusMoves, severeMoves, 0..19)
        val middlegameDamage = phaseScore(focusMoves, severeMoves, 20..59)
        val endgameDamage = phaseScore(focusMoves, severeMoves, 60..Int.MAX_VALUE)
        val worstPhase = listOf(
            "opening" to openingDamage,
            "middlegame" to middlegameDamage,
            "endgame" to endgameDamage
        ).maxByOrNull { it.second }?.first ?: "middlegame"

        val practicalAccuracy = ((stableMoves.toFloat() / focusMoves.size.toFloat()) * 100f).toInt()
        val dominantTheme = dominantTheme(
            worstPhase = worstPhase,
            blunders = blunders,
            mistakes = mistakes,
            missedBest = missedBest
        )

        val strengths = buildList {
            if (stableMoves > 0) {
                add("You kept $stableMoves of ${focusMoves.size} moves inside the engine's acceptable range.")
            }
            if (bestMoves > 0) {
                add("You found $bestMoves engine-best moves, which means your candidate move selection is working at times.")
            }
            if (practicalAccuracy >= 70) {
                add("Your baseline was stable enough that the game was not lost on every decision.")
            } else if (practicalAccuracy >= 55) {
                add("You held several positions together before the larger mistakes hit.")
            }
        }.ifEmpty {
            listOf("There were still a few playable decisions, but the review is dominated by preventable losses.")
        }

        val weaknesses = buildList {
            if (blunders > 0) {
                val biggest = biggestMiss?.let {
                    " Biggest miss: ${it.san} dropped about ${"%.1f".format(it.cpLoss / 100f)} pawns."
                }.orEmpty()
                add("$blunders blunder(s) decided the game more than the small inaccuracies did.$biggest")
            }
            if (mistakes > 0) {
                add("$mistakes mistake(s) bled significant evaluation before the position fully collapsed.")
            }
            if (inaccuracies >= 3) {
                add("$inaccuracies inaccuracies show recurring precision problems, even when the move was not a full blunder.")
            }
            if (missedBest >= (focusMoves.size / 2).coerceAtLeast(3)) {
                add("You missed stronger continuations on $missedBest moves, so calculation depth is the recurring bottleneck.")
            }
            add("The $worstPhase was your weakest phase in this review and should be the next training target.")
        }.distinct().take(4)

        val summaryText = buildString {
            append("This game was mainly lost in the $worstPhase through $dominantTheme. ")
            append("Your practical accuracy was $practicalAccuracy%. ")
            append(
                when {
                    blunders > 0 -> "Cut the blunders first; that will improve results faster than polishing small inaccuracies."
                    mistakes > 0 -> "Clean up the medium-sized mistakes before worrying about deeper engine lines."
                    else -> "The base level is acceptable; the next gain comes from converting more moves into best or excellent choices."
                }
            )
        }

        return CoachInsight(
            id = 0,
            weaknesses = weaknesses,
            strengths = strengths.take(3),
            youtubeLinks = practiceSuggestions(worstPhase, dominantTheme),
            summaryText = summaryText
        )
    }

    private fun dominantTheme(
        worstPhase: String,
        blunders: Int,
        mistakes: Int,
        missedBest: Int
    ): String = when {
        blunders >= 2 -> "large tactical oversights"
        mistakes >= 2 -> "repeat calculation errors"
        missedBest >= 4 -> "shallow candidate evaluation"
        worstPhase == "endgame" -> "late-game conversion mistakes"
        else -> "middlegame decision quality"
    }

    private fun practiceSuggestions(
        worstPhase: String,
        dominantTheme: String
    ): List<YoutubeSuggestion> {
        val phaseSuggestion = when (worstPhase) {
            "opening" -> YoutubeSuggestion(
                title = "Opening discipline and early middlegame plans",
                channel = "YouTube Search",
                searchQuery = "chess opening principles punish early mistakes"
            )
            "endgame" -> YoutubeSuggestion(
                title = "Basic endgame conversion drills",
                channel = "YouTube Search",
                searchQuery = "chess endgame fundamentals conversion technique"
            )
            else -> YoutubeSuggestion(
                title = "Middlegame planning and tactical calculation",
                channel = "YouTube Search",
                searchQuery = "chess middlegame planning tactical calculation drills"
            )
        }

        val themeSuggestion = when (dominantTheme) {
            "large tactical oversights" -> YoutubeSuggestion(
                title = "Blunder-check routine before every move",
                channel = "YouTube Search",
                searchQuery = "chess blunder check routine hanging pieces tactics"
            )
            "late-game conversion mistakes" -> YoutubeSuggestion(
                title = "Simplify winning endgames cleanly",
                channel = "YouTube Search",
                searchQuery = "chess simplify winning positions endgame technique"
            )
            else -> YoutubeSuggestion(
                title = "Improve candidate move calculation",
                channel = "YouTube Search",
                searchQuery = "chess candidate moves calculation training"
            )
        }

        return listOf(phaseSuggestion, themeSuggestion).distinctBy { it.searchQuery }
    }

    private fun phaseScore(
        focusMoves: List<ReviewMoveResult>,
        severeMoves: List<ReviewMoveResult>,
        range: IntRange
    ): Int {
        val severeCount = severeMoves.count { it.ply in range }
        if (severeCount > 0) return severeCount * 10

        return focusMoves.count {
            it.ply in range && it.classification == MoveClassification.Inaccuracy
        }
    }
}
