package com.chessigma.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BundledPuzzleDao {
    @Query("SELECT * FROM bundled_puzzles WHERE rating BETWEEN :minRating AND :maxRating ORDER BY RANDOM() LIMIT :limit")
    fun getPuzzlesByRating(minRating: Int, maxRating: Int, limit: Int = 50): Flow<List<BundledPuzzleEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPuzzles(puzzles: List<BundledPuzzleEntity>)

    @Update
    suspend fun updatePuzzle(puzzle: BundledPuzzleEntity)
}
