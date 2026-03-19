package com.chessigma.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "elo_history")
data class EloHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platform: String,
    val date: Long,
    val elo: Int
)
