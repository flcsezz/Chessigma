package com.chessigma.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // Repositories are currently injected via constructor injection 
    // (@Inject constructor) so they don't explicitly need provider setups 
    // here unless they are interfaces with implementations.
    // E.g. @Binds fun bindAiRepository(impl: AiRepositoryImpl): AiRepository
}
