package com.chessigma.app.ui.play.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chessigma.app.domain.model.ChessPiece
import com.chessigma.app.domain.model.PieceColor

@Composable
fun PlayerCard(
    name: String,
    color: PieceColor,
    capturedPieces: List<ChessPiece>,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    materialAdvantage: Int = 0,
    avatar: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = avatar ?: name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                
                if (materialAdvantage > 0) {
                    Text(
                        text = "+$materialAdvantage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Captures
            if (capturedPieces.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-4).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    capturedPieces.forEach { piece ->
                        Text(
                            text = piece.unicodeSymbol,
                            fontSize = 16.sp,
                            color = if (piece.color == PieceColor.WHITE) Color.White else Color.Black
                        )
                    }
                }
            }
        }

        if (isActive) {
            Surface(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            ) {}
        }
    }
}
