package com.chessigma.app.di

import com.chessigma.app.data.repository.LocalGameRepositoryImpl
import com.chessigma.app.data.repository.StatsRepositoryImpl
import com.chessigma.app.data.repository.SettingsRepositoryImpl
import com.chessigma.app.data.repository.SupabaseAuthRepositoryImpl
import com.chessigma.app.domain.repository.LocalGameRepository
import com.chessigma.app.domain.repository.StatsRepository
import com.chessigma.app.domain.repository.SettingsRepository
import com.chessigma.app.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLocalGameRepository(
        impl: LocalGameRepositoryImpl
    ): LocalGameRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(
        impl: StatsRepositoryImpl
    ): StatsRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: SupabaseAuthRepositoryImpl
    ): AuthRepository
}
