package com.chessigma.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY datePlayed DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameById(id: String): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("UPDATE games SET result = :result WHERE id = :gameId")
    suspend fun updateResult(gameId: String, result: String)

    @Query("UPDATE games SET isAnalysed = 1 WHERE id = :gameId")
    suspend fun markAnalysed(gameId: String)

    @Query("UPDATE games SET accuracyWhite = :whiteAccuracy, accuracyBlack = :blackAccuracy WHERE id = :gameId")
    suspend fun updateAccuracy(gameId: String, whiteAccuracy: Float, blackAccuracy: Float)
}
