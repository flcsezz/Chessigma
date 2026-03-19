package com.chessigma.app.domain.model

data class BotConfig(
    val name: String,
    val avatarRes: Int,
    val personalityTagline: String,
    val eloRating: Int,
    val stockfishDepth: Int,
    val stockfishSkillLevel: Int
) {
    companion object {
        val predefinedBots = listOf(
            BotConfig("Novice", 0, "Just learning the ropes...", 400, 1, 0),
            BotConfig("Apprentice", 0, "Getting the hang of it.", 800, 3, 3),
            BotConfig("Club Player", 0, "Solid middle game.", 1200, 6, 7),
            BotConfig("Tactician", 0, "Beware my traps!", 1500, 10, 12),
            BotConfig("Expert", 0, "Calculation is my middle name.", 1800, 15, 17),
            BotConfig("Master", 0, "Do you have what it takes?", 2200, 20, 20)
        )
    }
}
