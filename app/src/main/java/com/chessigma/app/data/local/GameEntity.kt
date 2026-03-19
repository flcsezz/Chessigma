package com.chessigma.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val platform: String, // CHESS_COM / LICHESS / LOCAL
    val pgn: String,
    val whiteName: String,
    val blackName: String,
    val whiteElo: Int?,
    val blackElo: Int?,
    val result: String,
    val datePlayed: Long,
    val openingEco: String?,
    val openingName: String?,
    val accuracyWhite: Float?,
    val accuracyBlack: Float?,
    val isAnalysed: Boolean = false,
    val userColor: String // WHITE / BLACK / UNKNOWN
)
