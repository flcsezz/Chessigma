package com.chessigma.app.ui.play.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chessigma.app.domain.model.ChessMove

@Composable
fun MoveList(
    moves: List<ChessMove>,
    modifier: Modifier = Modifier,
    activeIndex: Int = -1,
    onMoveClick: (Int) -> Unit = {}
) {
    val listState = rememberLazyListState()

    // Auto-scroll to the end when a new move is added
    LaunchedEffect(moves.size) {
        if (moves.isNotEmpty()) {
            listState.animateScrollToItem(moves.size)
        }
    }

    // Group moves by turn (White, Black)
    val turns = moves.chunked(2)

    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
        contentPadding = PaddingValues(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(turns) { index, turnMoves ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
                
                // White move
                MoveItem(
                    text = turnMoves[0].san ?: "${turnMoves[0].fromSquare}-${turnMoves[0].toSquare}",
                    isSelected = index * 2 == activeIndex,
                    onClick = { onMoveClick(index * 2) }
                )
                
                // Black move (if exists)
                if (turnMoves.size > 1) {
                    MoveItem(
                        text = turnMoves[1].san ?: "${turnMoves[1].fromSquare}-${turnMoves[1].toSquare}",
                        isSelected = index * 2 + 1 == activeIndex,
                        onClick = { onMoveClick(index * 2 + 1) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MoveItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            letterSpacing = 0.5.sp
        )
    }
}
