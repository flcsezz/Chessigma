package com.chessigma.app.ui.play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chessigma.app.domain.model.*
import com.chessigma.app.ui.components.Chessboard
import com.chessigma.app.ui.components.rememberChessboardState
import com.chessigma.app.ui.play.components.PromotionDialog
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextAlign


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.chessigma.app.ui.play.components.EvalBar
import com.chessigma.app.ui.play.components.MoveList
import com.chessigma.app.ui.play.components.PlayerCard
import com.chessigma.app.ui.util.bounceClick
import com.chessigma.app.ui.util.screenEntryTransition

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
    val boardState = rememberChessboardState(
        boardTheme = uiState.settings.boardTheme,
        pieceSet = uiState.settings.pieceSet
    )
    boardState.board = uiState.gameState.board
    boardState.legalMoves = uiState.legalMoves
    boardState.lastMove = uiState.gameState.moveHistory.lastOrNull()
    boardState.selectedSquare = uiState.selectedSquare
    boardState.boardTheme = uiState.settings.boardTheme
    boardState.pieceSet = uiState.settings.pieceSet

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    if (uiState.isPromotionRequired) {
        PromotionDialog(
            color = uiState.gameState.board.sideToMove,
            onPieceSelected = onPromotionSelected,
            onDismiss = onPromotionCancelled
        )
    }

    if (uiState.isExtractingEngine) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Optimizing Engine",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LinearProgressIndicator(
                        progress = { uiState.engineExtractionProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    Text(
                        "${(uiState.engineExtractionProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Unpacking Stockfish for peak performance. This only happens once.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
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
                        modifier = Modifier.fillMaxWidth().bounceClick()
                    ) {
                        Text("Local Play (PVP)")
                    }
                    Button(
                        onClick = {
                            onStartVsCpuGame(PieceColor.BLACK, 5) // Simple default for now
                            showNewGameDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().bounceClick()
                    ) {
                        Text("Vs CPU (Level 5)")
                    }
                }
            },
            confirmButton = {}
        )
    }

    screenEntryTransition(visible = visible) {
        // Subtle background gradient for luxury feel
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chessigma",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = { showNewGameDialog = true },
                            modifier = Modifier.bounceClick()
                        ) {
                            Text("New Game")
                        }
                        TextButton(
                            onClick = onUndo,
                            enabled = uiState.gameState.fenHistory.isNotEmpty(),
                            modifier = Modifier.bounceClick()
                        ) {
                            Text("Undo")
                        }
                    }
                }

                // Player 2 (Black)
                val opponentName = if (uiState.isVsCpu) "Stockfish Lvl ${uiState.skillLevel}" else "Opponent"
                PlayerCard(
                    name = opponentName,
                    color = PieceColor.BLACK,
                    capturedPieces = uiState.blackCaptures,
                    isActive = uiState.gameState.board.sideToMove == PieceColor.BLACK,
                    materialAdvantage = uiState.blackMaterialAdvantage,
                    avatar = if (uiState.isVsCpu) "AI" else "P2"
                )

                // Board and Eval Bar Container
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Eval bar encapsulated
                    Box(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                    ) {
                        EvalBar(eval = uiState.evaluation)
                    }
                    
                    // The Board itself
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

                // Status and Coach Card
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

                // Coach Insight Card - sleek and modern
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("✦", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                                }
                            }
                            Text(
                                text = "AI Coach Insight",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        uiState.statusMessage?.let { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        CascadeStatus(state = uiState.cascadeState)
                    }
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
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GAME OVER",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = status.name.replace("_", " "),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Button(
                onClick = onReviewGame,
                modifier = Modifier.fillMaxWidth().bounceClick(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Review Game", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun CascadeStatus(state: AiCascadeState) {
    when (state) {
        AiCascadeState.Idle -> {
            Text("Awaiting next move...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        is AiCascadeState.Loading -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Analyzing via ${state.provider}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        is AiCascadeState.Success -> {
            Text(
                text = state.insight.summaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        is AiCascadeState.RateLimited -> {
            Text(
                text = "${state.provider} limit reached. Switching provider...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        AiCascadeState.AllProvidersFailed -> {
            Text(
                text = "Coach offline.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        AiCascadeState.Offline -> {
            Text(
                text = "Coach requires internet.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
