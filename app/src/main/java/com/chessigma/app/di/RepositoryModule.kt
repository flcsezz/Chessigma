package com.chessigma.app.di

import com.chessigma.app.data.repository.LocalGameRepositoryImpl
import com.chessigma.app.domain.repository.LocalGameRepository
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
}
