package com.chessigma.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EloHistoryDao {
    @Query("SELECT * FROM elo_history ORDER BY date ASC")
    fun getEloHistory(): Flow<List<EloHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElo(elo: EloHistoryEntity)
}
