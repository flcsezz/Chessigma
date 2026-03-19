package com.chessigma.app.data.remote.api

import retrofit2.http.POST

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(): Any // Stub
}

interface GroqApiService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(): Any // Stub
}

interface NvidiaApiService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(): Any // Stub
}

interface ChessComApiService {
    // Stub
}

interface LichessApiService {
    // Stub
}
