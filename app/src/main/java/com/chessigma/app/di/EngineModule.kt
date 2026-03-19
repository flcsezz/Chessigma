package com.chessigma.app.di

import com.chessigma.app.engine.StockfishEngine
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @Provides
    @Singleton
    fun provideStockfishEngine(@ApplicationContext context: Context): StockfishEngine {
        return StockfishEngine(context)
    }
}
