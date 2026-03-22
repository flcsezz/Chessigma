package com.chessigma.app.ui.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.bounceClick() = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "BounceClickScale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

@Composable
fun screenEntryTransition(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn(animationSpec = tween(500)) +
                androidx.compose.animation.slideInVertically(
                    initialOffsetY = { it / 10 },
                    animationSpec = tween(500, easing = EaseOutCubic)
                ),
        exit = androidx.compose.animation.fadeOut(animationSpec = tween(300))
    ) {
        content()
    }
}
