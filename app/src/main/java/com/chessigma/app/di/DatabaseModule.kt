package com.chessigma.app.di

import android.content.Context
import androidx.room.Room
import com.chessigma.app.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "chessigma_db"
        ).build()
    }

    @Provides
    fun provideGameDao(database: AppDatabase): GameDao = database.gameDao()

    @Provides
    fun provideMoveDao(database: AppDatabase): MoveDao = database.moveDao()

    @Provides
    fun providePersonalPuzzleDao(database: AppDatabase): PersonalPuzzleDao = database.personalPuzzleDao()

    @Provides
    fun provideBundledPuzzleDao(database: AppDatabase): BundledPuzzleDao = database.bundledPuzzleDao()

    @Provides
    fun provideCoachInsightDao(database: AppDatabase): CoachInsightDao = database.coachInsightDao()

    @Provides
    fun provideEloHistoryDao(database: AppDatabase): EloHistoryDao = database.eloHistoryDao()

    @Provides
    fun provideBotGameDao(database: AppDatabase): BotGameDao = database.botGameDao()
}
