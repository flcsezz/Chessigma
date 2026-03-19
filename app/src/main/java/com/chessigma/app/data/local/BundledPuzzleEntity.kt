package com.chessigma.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bundled_puzzles")
data class BundledPuzzleEntity(
    @PrimaryKey val id: String, // lichess puzzle id
    val fen: String,
    val moves: String, // space-separated UCI
    val rating: Int,
    val themes: String, // comma-separated
    val solvedByUser: Boolean = false,
    val solvedAt: Long?
)
