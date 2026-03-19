package com.chessigma.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "personal_puzzles")
data class PersonalPuzzleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceGameId: String?,
    val ply: Int,
    val fenPosition: String,
    val correctUci: String,
    val correctSan: String,
    val blunderSan: String,
    val originalClassification: String,
    val solved: Boolean = false,
    val solvedAt: Long?,
    val attemptCount: Int = 0,
    val createdAt: Long
)
