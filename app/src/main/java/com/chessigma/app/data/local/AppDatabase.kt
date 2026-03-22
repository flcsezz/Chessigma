package com.chessigma.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        GameEntity::class,
        MoveEntity::class,
        PersonalPuzzleEntity::class,
        BundledPuzzleEntity::class,
        CoachInsightEntity::class,
        EloHistoryEntity::class,
        BotGameEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun moveDao(): MoveDao
    abstract fun personalPuzzleDao(): PersonalPuzzleDao
    abstract fun bundledPuzzleDao(): BundledPuzzleDao
    abstract fun coachInsightDao(): CoachInsightDao
    abstract fun eloHistoryDao(): EloHistoryDao
    abstract fun botGameDao(): BotGameDao
}
