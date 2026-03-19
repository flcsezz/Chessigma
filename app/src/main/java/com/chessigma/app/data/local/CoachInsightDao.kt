package com.chessigma.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoachInsightDao {
    @Query("SELECT * FROM coach_insights ORDER BY generatedAt DESC LIMIT 1")
    fun getLatestInsight(): Flow<CoachInsightEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: CoachInsightEntity)
}
