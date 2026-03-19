package com.chessigma.app.data.remote.dto

import kotlinx.serialization.Serializable

// ── Gemini ────────────────────────────────────────────────────────────────────

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent
)

// ── Groq & NVIDIA (OpenAI Compatible) ────────────────────────────────────────

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<ChatChoice>
)

@Serializable
data class ChatChoice(
    val message: ChatMessage
)
