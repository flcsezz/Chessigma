package com.chessigma.app.domain.model

import androidx.compose.ui.graphics.Color

enum class BoardTheme(
    val displayName: String,
    val lightSquare: Color,
    val darkSquare: Color,
    val selectedSquare: Color,
    val lastMoveSquare: Color
) {
    WOOD(
        "Classic Wood",
        Color(0xFFE7E5E4), // Stone 200
        Color(0xFF854D0E).copy(alpha = 0.8f), // Yellow 800
        Color(0xFFCA8A04).copy(alpha = 0.5f), // Yellow 600
        Color(0xFF10B981).copy(alpha = 0.3f)  // Emerald 500
    ),
    EMERALD(
        "Tournament Green",
        Color(0xFFEBECD0),
        Color(0xFF779556),
        Color(0xFFF6F669).copy(alpha = 0.5f),
        Color(0xFFB9CA43).copy(alpha = 0.5f)
    ),
    MIDNIGHT(
        "Midnight Blue",
        Color(0xFF94A3B8), // Slate 400
        Color(0xFF1E293B), // Slate 800
        Color(0xFF38B2AC).copy(alpha = 0.5f), // Teal
        Color(0xFF6366F1).copy(alpha = 0.4f)  // Indigo
    ),
    OCEAN(
        "Ocean Breeze",
        Color(0xFFBEE3F8),
        Color(0xFF2B6CB0),
        Color(0xFFF6E05E).copy(alpha = 0.5f),
        Color(0xFF4299E1).copy(alpha = 0.4f)
    )
}

enum class PieceSet(val displayName: String) {
    DEFAULT("Modern"),
    NEON("Neon Glow"),
    STARK("High Contrast")
}
