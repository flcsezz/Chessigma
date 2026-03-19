package com.chessigma.app.domain.model

sealed class MoveClassification(
    val displayName: String,
    val colorHex: String,
    val cpThreshold: Int
) {
    object Brilliant : MoveClassification("Brilliant", "#21AFDB", 0) // Special logic
    object Best : MoveClassification("Best Move", "#4A9B6F", 10)
    object Excellent : MoveClassification("Excellent", "#4A9B6F", 20)
    object Good : MoveClassification("Good", "#4A9B6F", 30)
    object Inaccuracy : MoveClassification("Inaccuracy", "#E6A817", 50)
    object Mistake : MoveClassification("Mistake", "#E6A817", 100)
    object Blunder : MoveClassification("Blunder", "#D9534F", 300)
    object Miss : MoveClassification("Miss", "#D9534F", 200)

    companion object {
        fun fromStorage(value: String): MoveClassification? = when (value.uppercase()) {
            "BRILLIANT" -> Brilliant
            "BEST" -> Best
            "EXCELLENT" -> Excellent
            "GOOD" -> Good
            "INACCURACY" -> Inaccuracy
            "MISTAKE" -> Mistake
            "BLUNDER" -> Blunder
            "MISS" -> Miss
            else -> null
        }
    }
}
