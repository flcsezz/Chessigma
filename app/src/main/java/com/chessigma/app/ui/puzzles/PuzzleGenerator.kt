package com.chessigma.app.ui.puzzles

import com.chessigma.app.data.local.MoveDao
import com.chessigma.app.data.local.PersonalPuzzleDao
import com.chessigma.app.data.local.PersonalPuzzleEntity
import javax.inject.Inject

class PuzzleGenerator @Inject constructor(
    private val moveDao: MoveDao,
    private val personalPuzzleDao: PersonalPuzzleDao
) {
    suspend fun generateFromGame(gameId: String) {
        val moves = moveDao.getMovesForGameSync(gameId)
        val mistakesAndBlunders = moves.filter { 
            it.classification == "BLUNDER" || it.classification == "MISTAKE" 
        }

        for (move in mistakesAndBlunders) {
            val bestUci = move.bestUci ?: continue
            val bestSan = move.bestSan ?: continue
            val fenBefore = move.fenBefore
            
            val puzzle = PersonalPuzzleEntity(
                sourceGameId = gameId,
                ply = move.ply,
                fenPosition = fenBefore,
                correctUci = bestUci,
                correctSan = bestSan,
                blunderSan = move.san,
                originalClassification = move.classification,
                solved = false,
                solvedAt = null,
                attemptCount = 0,
                createdAt = System.currentTimeMillis()
            )
            personalPuzzleDao.insertPersonalPuzzle(puzzle)
        }
    }
}
