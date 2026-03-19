package com.chessigma.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coach_insights")
data class CoachInsightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val generatedAt: Long,
    val gamesAnalysed: Int,
    val weaknessesJson: String,
    val strengthsJson: String,
    val youtubeLinksJson: String,
    val summaryText: String,
    val rawApiResponse: String
)
