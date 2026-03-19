package com.chessigma.app.di

import com.chessigma.app.data.remote.api.ChessComApiService
import com.chessigma.app.data.remote.api.GeminiApiService
import com.chessigma.app.data.remote.api.GroqApiService
import com.chessigma.app.data.remote.api.LichessApiService
import com.chessigma.app.data.remote.api.NvidiaApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("Gemini")
    fun provideGeminiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGeminiApiService(@Named("Gemini") retrofit: Retrofit): GeminiApiService {
        return retrofit.create(GeminiApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("Groq")
    fun provideGroqRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGroqApiService(@Named("Groq") retrofit: Retrofit): GroqApiService {
        return retrofit.create(GroqApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("Nvidia")
    fun provideNvidiaRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://integrate.api.nvidia.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNvidiaApiService(@Named("Nvidia") retrofit: Retrofit): NvidiaApiService {
        return retrofit.create(NvidiaApiService::class.java)
    }

    // Define ChessCom and Lichess retrofits stub
    @Provides
    @Singleton
    fun provideChessComApi(): ChessComApiService {
        return Retrofit.Builder().baseUrl("https://api.chess.com/").build().create(ChessComApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLichessApi(): LichessApiService {
        return Retrofit.Builder().baseUrl("https://lichess.org/api/").build().create(LichessApiService::class.java)
    }
}
