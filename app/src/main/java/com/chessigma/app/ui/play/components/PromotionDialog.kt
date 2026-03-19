package com.chessigma.app.ui.play.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chessigma.app.domain.model.PieceColor
import com.chessigma.app.domain.model.PieceType

@Composable
fun PromotionDialog(
    color: PieceColor,
    onPieceSelected: (PieceType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Promotion Piece") },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PromotionItem(PieceType.QUEEN, color, onPieceSelected)
                PromotionItem(PieceType.KNIGHT, color, onPieceSelected)
                PromotionItem(PieceType.ROOK, color, onPieceSelected)
                PromotionItem(PieceType.BISHOP, color, onPieceSelected)
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PromotionItem(
    type: PieceType,
    color: PieceColor,
    onSelected: (PieceType) -> Unit
) {
    val pieceChar = when (type) {
        PieceType.QUEEN -> if (color == PieceColor.WHITE) "♕" else "♛"
        PieceType.KNIGHT -> if (color == PieceColor.WHITE) "♘" else "♞"
        PieceType.ROOK -> if (color == PieceColor.WHITE) "♖" else "♜"
        PieceType.BISHOP -> if (color == PieceColor.WHITE) "♗" else "♝"
        else -> ""
    }

    Box(
        modifier = Modifier
            .size(64.dp)
            .clickable { onSelected(type) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = pieceChar,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
