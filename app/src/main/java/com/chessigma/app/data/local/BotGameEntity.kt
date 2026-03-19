package com.chessigma.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bot_games")
data class BotGameEntity(
    @PrimaryKey val id: String, // UUID
    val botName: String,
    val botElo: Int,
    val userColor: String,
    val result: String,
    val pgn: String,
    val accuracyUser: Float?,
    val datePlayed: Long,
    val reviewShown: Boolean = false
)
