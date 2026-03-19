package com.chessigma.app.data.remote.api

import com.chessigma.app.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

interface GroqApiService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

interface NvidiaApiService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

interface ChessComApiService {
    // Stub
}

interface LichessApiService {
    @retrofit2.http.GET("puzzle/daily")
    suspend fun getDailyPuzzle(): LichessPuzzleResponse
}
