package com.chessigma.app.di

import com.chessigma.app.engine.StockfishEngine
import com.chessigma.app.data.repository.EngineRepositoryImpl
import com.chessigma.app.domain.repository.EngineRepository
import com.chessigma.app.domain.usecase.AnalyzePositionUseCase
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EngineModule {

    @Binds
    @Singleton
    abstract fun bindEngineRepository(
        engineRepositoryImpl: EngineRepositoryImpl
    ): EngineRepository

    companion object {
        @Provides
        @Singleton
        fun provideStockfishEngine(@ApplicationContext context: Context): StockfishEngine {
            return StockfishEngine(context)
        }

        @Provides
        @Singleton
        fun provideAnalyzePositionUseCase(engineRepository: EngineRepository): AnalyzePositionUseCase {
            return AnalyzePositionUseCase(engineRepository)
        }
    }
}
