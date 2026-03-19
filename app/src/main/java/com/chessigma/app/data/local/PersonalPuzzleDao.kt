package com.chessigma.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalPuzzleDao {
    @Query("SELECT * FROM personal_puzzles ORDER BY createdAt DESC")
    fun getAllPersonalPuzzles(): Flow<List<PersonalPuzzleEntity>>

    @Transaction
    @Query("SELECT * FROM personal_puzzles ORDER BY createdAt DESC")
    fun getPuzzlesWithGames(): Flow<List<PersonalPuzzleWithGame>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPersonalPuzzle(puzzle: PersonalPuzzleEntity)

    @Update
    suspend fun updatePersonalPuzzle(puzzle: PersonalPuzzleEntity)
}
