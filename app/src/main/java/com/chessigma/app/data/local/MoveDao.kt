package com.chessigma.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MoveDao {
    @Query("SELECT * FROM moves WHERE gameId = :gameId ORDER BY ply ASC")
    fun getMovesForGame(gameId: String): Flow<List<MoveEntity>>
    
    @Query("SELECT * FROM moves WHERE gameId = :gameId ORDER BY ply ASC")
    suspend fun getMovesForGameSync(gameId: String): List<MoveEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMove(move: MoveEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoves(moves: List<MoveEntity>)
}
