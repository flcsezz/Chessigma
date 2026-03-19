package com.chessigma.app.domain.repository

import com.chessigma.app.data.local.GameEntity
import com.chessigma.app.data.local.MoveEntity
import com.chessigma.app.domain.model.ChessMove
import kotlinx.coroutines.flow.Flow

/**
 * Domain-layer contract for local game persistence.
 * No Android-specific types — all I/O goes through DAOs in the data layer.
 */
interface LocalGameRepository {
    /** Insert a new game row when a session starts. */
    suspend fun createGame(gameId: String, startedAt: Long)

    /** Persist a single move after it is confirmed legal. */
    suspend fun saveMove(
        gameId: String,
        ply: Int,
        move: ChessMove,
        fenBefore: String
    )

    /** Update the game result when the game ends (e.g. "1-0", "0-1", "1/2-1/2"). */
    suspend fun finalizeGame(gameId: String, result: String)

    /** Load a raw game + its move rows for the review pipeline (nullable if not found). */
    suspend fun loadGame(gameId: String): Pair<GameEntity, List<MoveEntity>>?

    /** Observe all saved games, newest first. */
    fun getRecentGames(): Flow<List<GameEntity>>

    /** Update per-move Stockfish review results. */
    suspend fun updateMoveReview(
        gameId: String,
        ply: Int,
        evalBefore: Int,
        evalAfter: Int,
        classification: String,
        bestUci: String?,
        bestSan: String?
    )

    /** Mark a game as fully analysed. */
    suspend fun markGameAnalysed(gameId: String)

    /** Update per-game accuracy stats (white and black). */
    suspend fun updateAccuracy(gameId: String, whiteAccuracy: Float, blackAccuracy: Float)
}
