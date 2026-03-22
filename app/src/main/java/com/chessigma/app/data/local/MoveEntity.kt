package com.chessigma.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "moves",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["gameId"])]
)
data class MoveEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameId: String,
    val ply: Int,
    val san: String,
    val uci: String,
    val fenBefore: String,
    val evalCpBefore: Int?,
    val evalCpAfter: Int?,
    val classification: String,
    val bestUci: String?,
    val bestSan: String?
)
