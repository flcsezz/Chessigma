package com.chessigma.app.ui.play.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun EvalBar(
    eval: Float, // Positive for White, negative for Black. 0.0 is equal.
    modifier: Modifier = Modifier
) {
    // Clamp eval to [-10, 10] for visualization purposes
    val clampedEval = eval.coerceIn(-10f, 10f)
    
    // Convert to percentage (0.0 to 1.0), where 0.5 is equal
    // clampedEval = -10 -> 0.0
    // clampedEval = 0 -> 0.5
    // clampedEval = 10 -> 1.0
    val targetFill = (clampedEval + 10f) / 20f
    
    val animatedFill by animateFloatAsState(targetValue = targetFill, label = "evalFill")

    Column(
        modifier = modifier
            .width(24.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black), // Black is for Black side
        verticalArrangement = Arrangement.Bottom
    ) {
        // White part (fills from bottom)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(animatedFill)
                .background(Color.White),
            contentAlignment = Alignment.TopCenter
        ) {
            if (clampedEval >= 0) {
                EvalText(eval = eval, color = Color.Black)
            }
        }
        
        // If eval is negative, show it in the black part
        if (clampedEval < 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                EvalText(eval = eval, color = Color.White)
            }
        }
    }
}

@Composable
private fun EvalText(eval: Float, color: Color) {
    val text = if (abs(eval) > 99) "M" else String.format("%.1f", abs(eval))
    Text(
        text = text,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
