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

    @Query("UPDATE personal_puzzles SET solved = 1, solvedAt = :solvedAt WHERE id = :puzzleId")
    suspend fun markSolved(puzzleId: Long, solvedAt: Long = System.currentTimeMillis())

    @Query("UPDATE personal_puzzles SET attemptCount = attemptCount + 1 WHERE id = :puzzleId")
    suspend fun incrementAttempts(puzzleId: Long)

    @Query("SELECT * FROM personal_puzzles WHERE id = :puzzleId")
    suspend fun getPuzzleById(puzzleId: Long): PersonalPuzzleEntity?
}
