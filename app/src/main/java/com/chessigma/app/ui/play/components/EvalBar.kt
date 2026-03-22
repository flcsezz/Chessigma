package com.chessigma.app.ui.play.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    val clampedEval = eval.coerceIn(-10f, 10f)
    // Non-linear scaling: y = 0.5 + 0.5 * (x / (abs(x) + 2.0))
    // This makes small advantages (e.g. +1.0) more visually significant.
    val targetFill = 0.5f + 0.5f * (clampedEval / (abs(clampedEval) + 2.0f))
    
    val animatedFill by animateFloatAsState(
        targetValue = targetFill,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "evalFill"
    )

    // Sleek premium colors
    val whiteColor = Color(0xFFFAFAF9) // Stone 50
    val blackColor = Color(0xFF1C1917) // Stone 900
    val borderColor = Color.White.copy(alpha = 0.1f)

    Column(
        modifier = modifier
            .width(28.dp) // slightly wider for presence
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(blackColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.Bottom
    ) {
        // White part (fills from bottom)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(animatedFill)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            whiteColor,
                            whiteColor.copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            if (clampedEval >= 0) {
                EvalText(eval = eval, color = blackColor)
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
                EvalText(eval = eval, color = whiteColor)
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
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}
