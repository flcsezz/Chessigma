package com.chessigma.app.domain.model

data class UserSettings(
    val geminiApiKey: String = "",
    val groqApiKey: String = "",
    val nvidiaApiKey: String = "",
    val preferredAiProvider: String = "GEMINI",
    val isDarkMode: Boolean = true,
    val stockfishDepth: Int = 12
)
