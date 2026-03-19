package com.chessigma.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.chessigma.app.domain.model.MoveClassification

fun MoveClassification.toColor(): Color {
    return Color(android.graphics.Color.parseColor(this.colorHex))
}
