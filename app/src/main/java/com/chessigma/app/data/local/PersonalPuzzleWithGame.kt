package com.chessigma.app.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class PersonalPuzzleWithGame(
    @Embedded val puzzle: PersonalPuzzleEntity,
    @Relation(
        parentColumn = "sourceGameId",
        entityColumn = "id"
    )
    val game: GameEntity
)
