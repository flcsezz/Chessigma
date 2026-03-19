package com.chessigma.app.ui.play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chessigma.app.domain.model.AiCascadeState
import com.chessigma.app.domain.model.ChessMove
import com.chessigma.app.domain.model.GameStatus
import com.chessigma.app.domain.model.PieceType
import com.chessigma.app.ui.components.Chessboard
import com.chessigma.app.ui.components.rememberChessboardState
import com.chessigma.app.ui.play.components.PromotionDialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.Alignment
import com.chessigma.app.domain.model.PieceColor
import com.chessigma.app.ui.play.components.EvalBar
import com.chessigma.app.ui.play.components.MoveList
import com.chessigma.app.ui.play.components.PlayerCard

@Composable
fun PlayRoute(
    viewModel: PlayViewModel = hiltViewModel(),
    onReviewGame: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    PlayScreen(
        uiState = uiState,
        onMove = viewModel::onMove,
        onSquareClick = viewModel::onSquareSelected,
        onUndo = viewModel::undo,
        onPromotionSelected = viewModel::onPromotionSelected,
        onPromotionCancelled = viewModel::onPromotionCancelled,
        onMoveHistoryClicked = viewModel::onMoveHistoryClicked,
        onReviewGame = onReviewGame,
        onStartVsCpuGame = viewModel::startVsCpuGame,
        onStartLocalGame = viewModel::startLocalGame,
        modifier = modifier
    )
}

@Composable
fun PlayScreen(
    uiState: PlayUiState,
    onMove: (ChessMove) -> Unit,
    onSquareClick: (String) -> Unit,
    onUndo: () -> Unit,
    onPromotionSelected: (PieceType) -> Unit,
    onPromotionCancelled: () -> Unit,
    onMoveHistoryClicked: (Int) -> Unit,
    onReviewGame: (String) -> Unit,
    onStartVsCpuGame: (PieceColor, Int) -> Unit,
    onStartLocalGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boardState = rememberChessboardState()
    boardState.board = uiState.gameState.board
    boardState.legalMoves = uiState.legalMoves
    boardState.lastMove = uiState.gameState.moveHistory.lastOrNull()
    boardState.selectedSquare = uiState.selectedSquare

    if (uiState.isPromotionRequired) {
        PromotionDialog(
            color = uiState.gameState.board.sideToMove,
            onPieceSelected = onPromotionSelected,
            onDismiss = onPromotionCancelled
        )
    }

    var showNewGameDialog by remember { mutableStateOf(false) }

    if (showNewGameDialog) {
        AlertDialog(
            onDismissRequest = { showNewGameDialog = false },
            title = { Text("New Game") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onStartLocalGame()
                            showNewGameDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Local Play (PVP)")
                    }
                    Button(
                        onClick = {
                            onStartVsCpuGame(PieceColor.BLACK, 5) // Simple default for now
                            showNewGameDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Vs CPU (Level 5)")
                    }
                }
            },
            confirmButton = {}
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Play",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Row {
                    TextButton(onClick = { showNewGameDialog = true }) {
                        Text("New Game")
                    }
                    TextButton(onClick = onUndo, enabled = uiState.gameState.fenHistory.isNotEmpty()) {
                        Text("Undo")
                    }
                }
            }

            // Player 2 (Black)
            val opponentName = if (uiState.isVsCpu) "Stockfish Level ${uiState.skillLevel}" else "Opponent"
            PlayerCard(
                name = opponentName,
                color = PieceColor.BLACK,
                capturedPieces = uiState.blackCaptures,
                isActive = uiState.gameState.board.sideToMove == PieceColor.BLACK,
                materialAdvantage = uiState.blackMaterialAdvantage,
                avatar = if (uiState.isVsCpu) "AI" else "P2"
            )

            // Board and Eval Bar
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EvalBar(
                    eval = uiState.evaluation,
                    modifier = Modifier.height(IntrinsicSize.Min)
                )
                
                Chessboard(
                    state = boardState,
                    onMove = onMove,
                    onSquareClick = onSquareClick,
                    modifier = Modifier.weight(1f)
                )
            }

            // Player 1 (White)
            PlayerCard(
                name = "Guest Player",
                color = PieceColor.WHITE,
                capturedPieces = uiState.whiteCaptures,
                isActive = uiState.gameState.board.sideToMove == PieceColor.WHITE,
                materialAdvantage = uiState.whiteMaterialAdvantage,
                avatar = "G"
            )

            // Move List
            MoveList(
                moves = uiState.gameState.moveHistory,
                activeIndex = uiState.historyIndex,
                onMoveClick = onMoveHistoryClicked,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Status and Coach
            AnimatedVisibility(
                visible = uiState.gameState.status != GameStatus.ONGOING,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                GameStatusBanner(
                    status = uiState.gameState.status,
                    onReviewGame = { onReviewGame(uiState.gameId) }
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    uiState.statusMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    CascadeStatus(state = uiState.cascadeState)
                }
            }
        }
    }
}

@Composable
private fun GameStatusBanner(
    status: GameStatus,
    onReviewGame: () -> Unit
) {
    if (status == GameStatus.ONGOING) return
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "GAME OVER: $status",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Button(
                onClick = onReviewGame,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Review Game")
            }
        }
    }
}

@Composable
private fun CascadeStatus(state: AiCascadeState) {
    when (state) {
        AiCascadeState.Idle -> {
            Text("Coach cascade idle.", style = MaterialTheme.typography.bodyMedium)
        }

        is AiCascadeState.Loading -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CircularProgressIndicator()
                Text(
                    text = "Generating coach insight via ${state.provider}...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        is AiCascadeState.Success -> {
            Text(
                text = "Coach verdict: ${state.insight.summaryText}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        is AiCascadeState.RateLimited -> {
            Text(
                text = "${state.provider} rate limited. Falling through provider cascade.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        AiCascadeState.AllProvidersFailed -> {
            Text(
                text = "Coach insight unavailable. All providers failed.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        AiCascadeState.Offline -> {
            Text(
                text = "Coach insight unavailable while offline.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
