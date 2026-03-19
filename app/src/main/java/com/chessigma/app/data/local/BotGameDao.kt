package com.chessigma.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BotGameDao {
    @Query("SELECT * FROM bot_games ORDER BY datePlayed DESC")
    fun getAllBotGames(): Flow<List<BotGameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBotGame(game: BotGameEntity)
}
