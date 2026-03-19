package com.chessigma.app.ui.review

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chessigma.app.domain.model.AiCascadeState
import com.chessigma.app.domain.model.CoachInsight
import com.chessigma.app.domain.model.MoveClassification
import com.chessigma.app.domain.model.ReviewMoveResult
import com.chessigma.app.domain.usecase.ParseFenUseCase
import com.chessigma.app.ui.components.Chessboard
import com.chessigma.app.ui.components.rememberChessboardState
import com.chessigma.app.ui.play.components.EvalBar
import kotlinx.coroutines.launch

@Composable
fun ReviewRoute(
    requestedGameId: String? = null,
    requestNonce: Int = 0,
    viewModel: ReviewViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(requestedGameId, requestNonce) {
        if (requestedGameId != null) {
            viewModel.startReview(requestedGameId)
        } else {
            viewModel.loadLatestGame()
        }
    }

    ReviewScreen(
        uiState = uiState,
        evalHistory = viewModel.evalHistory,
        onSelectPly = viewModel::selectPly,
        onStartReview = viewModel::startReview,
        modifier = modifier
    )
}

@Composable
fun ReviewScreen(
    uiState: ReviewUiState,
    evalHistory: List<Float>,
    onSelectPly: (Int) -> Unit,
    onStartReview: (String) -> Unit,
    modifier: Modifier = Modifier,
    parseFen: ParseFenUseCase = ParseFenUseCase()
) {
    val reviewState = uiState.reviewState
    val moves = (reviewState as? ReviewState.Done)?.moves ?: emptyList()

    // Resolve the board FEN for the selected ply
    val boardFen = remember(uiState.selectedPly, moves) {
        if (moves.isEmpty()) "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        else moves.getOrNull(uiState.selectedPly)?.fenBefore
            ?: moves.lastOrNull()?.fenBefore
            ?: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    }

    val board = remember(boardFen) { parseFen(boardFen) }
    val boardState = rememberChessboardState()
    boardState.board = board

    // Eval at selected ply (for the eval bar)
    val currentEval = remember(uiState.selectedPly, moves) {
        moves.getOrNull(uiState.selectedPly)?.evalCpBefore?.div(100f) ?: 0f
    }

    val scope = rememberCoroutineScope()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            ReviewHeader(
                reviewState = reviewState,
                moves = moves,
                currentGame = uiState.currentGame
            )

            // ── Board + Eval bar ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EvalBar(
                    eval = currentEval,
                    modifier = Modifier.height(IntrinsicSize.Min)
                )
                Chessboard(
                    state = boardState,
                    onMove = {},
                    onSquareClick = {},
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Eval Sparkline ────────────────────────────────────────────────
            if (evalHistory.size >= 2) {
                EvalSparkline(
                    evals = evalHistory,
                    selectedIndex = uiState.selectedPly,
                    onTap = { onSelectPly(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }

            // ── Navigation arrows ─────────────────────────────────────────────
            if (moves.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onSelectPly((uiState.selectedPly - 1).coerceAtLeast(0)) },
                        enabled = uiState.selectedPly > 0
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous move")
                    }
                    Text(
                        text = "Move ${uiState.selectedPly + 1} / ${moves.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(
                        onClick = { onSelectPly((uiState.selectedPly + 1).coerceAtMost(moves.size - 1)) },
                        enabled = uiState.selectedPly < moves.size - 1
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next move")
                    }
                }
            }

            // ── Annotated Move List ────────────────────────────────────────────
            ReviewMoveList(
                moves = moves,
                selectedPly = uiState.selectedPly,
                onMoveTap = onSelectPly,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 120.dp)
            )

            // ── Progress / Idle banner ─────────────────────────────────────────
            when (reviewState) {
                is ReviewState.Analysing -> {
                    val progress = if (reviewState.totalPlies > 0)
                        reviewState.completedPlies.toFloat() / reviewState.totalPlies.toFloat()
                    else 0f
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Analysing… ${reviewState.completedPlies}/${reviewState.totalPlies} moves",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                is ReviewState.Error -> {
                    Text(
                        "Error: ${reviewState.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is ReviewState.Idle -> {
                    GameHistoryList(
                        games = uiState.recentGames,
                        onGameClick = onStartReview,
                        modifier = Modifier.weight(1f)
                    )
                }
                else -> Unit
            }

            CoachInsightSection(uiState.coachState)
        }
    }
}

// ── Header ─────────────────────────────────────────────────────────────────────

@Composable
private fun ReviewHeader(
    reviewState: ReviewState,
    moves: List<ReviewMoveResult>,
    currentGame: com.chessigma.app.data.local.GameEntity? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Game Review",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Show accuracy if analysis is done, OR if the game was previously analysed
        if ((reviewState is ReviewState.Done && moves.isNotEmpty()) || currentGame?.isAnalysed == true) {
            val (whiteAcc, blackAcc) = if (reviewState is ReviewState.Done && moves.isNotEmpty()) {
                computeAccuracy(moves)
            } else {
                (currentGame?.accuracyWhite?.toInt() ?: 0) to (currentGame?.accuracyBlack?.toInt() ?: 0)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AccuracyChip("W", whiteAcc, Color.White)
                AccuracyChip("B", blackAcc, Color(0xFF666666))
            }
        }
    }
}

@Composable
private fun AccuracyChip(label: String, accuracy: Int, bg: Color) {
    val textColor = if (bg == Color.White) Color.Black else Color.White
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg.copy(alpha = 0.15f))
            .border(1.dp, bg.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$label $accuracy%",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Eval Sparkline ─────────────────────────────────────────────────────────────

@Composable
private fun EvalSparkline(
    evals: List<Float>,
    selectedIndex: Int,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val whiteColor = Color(0xFFF0D9B5)
    val blackColor = Color(0xFF4A4A4A)
    val lineColor = MaterialTheme.colorScheme.primary
    val cursorColor = MaterialTheme.colorScheme.tertiary

    Canvas(modifier = modifier
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        if (evals.size < 2) return@Canvas

        val w = size.width
        val h = size.height
        val midY = h / 2f
        val stepX = w / (evals.size - 1).coerceAtLeast(1).toFloat()

        // Zero line
        drawLine(
            color = Color.Gray.copy(alpha = 0.4f),
            start = Offset(0f, midY),
            end = Offset(w, midY),
            strokeWidth = 1.dp.toPx()
        )

        // Fill path
        val path = Path()
        evals.forEachIndexed { i, ev ->
            val x = i * stepX
            val clamped = ev.coerceIn(-10f, 10f)
            val y = midY - (clamped / 10f) * midY
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        // Close fill at bottom
        val fillPath = Path().apply {
            addPath(path)
            lineTo((evals.size - 1) * stepX, h)
            lineTo(0f, h)
            close()
        }
        drawPath(fillPath, color = lineColor.copy(alpha = 0.15f))
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Cursor at selected ply
        val cx = selectedIndex.coerceIn(0, evals.size - 1) * stepX
        val cEval = evals.getOrElse(selectedIndex) { 0f }.coerceIn(-10f, 10f)
        val cy = midY - (cEval / 10f) * midY
        drawLine(
            color = cursorColor.copy(alpha = 0.6f),
            start = Offset(cx, 0f),
            end = Offset(cx, h),
            strokeWidth = 1.5.dp.toPx()
        )
        drawCircle(color = cursorColor, radius = 4.dp.toPx(), center = Offset(cx, cy))
    }
}

// ── Annotated Move List ─────────────────────────────────────────────────────────

@Composable
private fun ReviewMoveList(
    moves: List<ReviewMoveResult>,
    selectedPly: Int,
    onMoveTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedPly) {
        if (moves.isNotEmpty()) {
            listState.animateScrollToItem(selectedPly.coerceIn(0, moves.size - 1))
        }
    }

    val turns = moves.chunked(2)

    LazyRow(
        state = listState,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        itemsIndexed(turns) { idx, pair ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    "${idx + 1}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                ReviewMoveChip(
                    result = pair[0],
                    isSelected = idx * 2 == selectedPly,
                    onClick = { onMoveTap(idx * 2) }
                )
                if (pair.size > 1) {
                    ReviewMoveChip(
                        result = pair[1],
                        isSelected = idx * 2 + 1 == selectedPly,
                        onClick = { onMoveTap(idx * 2 + 1) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewMoveChip(
    result: ReviewMoveResult,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val classColor = Color(android.graphics.Color.parseColor(result.classification.colorHex))
    val bg = if (isSelected) classColor.copy(alpha = 0.3f)
             else MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = classColor.copy(alpha = if (isSelected) 1f else 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = result.san,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = if (isSelected) classColor else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ── Accuracy calculation ───────────────────────────────────────────────────────

private fun computeAccuracy(moves: List<ReviewMoveResult>): Pair<Int, Int> {
    if (moves.isEmpty()) return 0 to 0
    val whiteMoves = moves.filter { it.ply % 2 == 0 }
    val blackMoves = moves.filter { it.ply % 2 == 1 }

    fun avgAccuracy(list: List<ReviewMoveResult>): Int {
        if (list.isEmpty()) return 100
        // Simple accuracy: percentage of moves graded Good or better
        val good = list.count { it.classification in setOf(
            MoveClassification.Best, MoveClassification.Brilliant,
            MoveClassification.Excellent, MoveClassification.Good
        )}
        return (good * 100) / list.size
    }

    return avgAccuracy(whiteMoves) to avgAccuracy(blackMoves)
}

@Composable
private fun CoachInsightSection(state: AiCascadeState) {
    when (state) {
        AiCascadeState.Idle -> Unit
        is AiCascadeState.Loading -> {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text(
                        text = "Building coach summary from reviewed moves...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        is AiCascadeState.Success -> {
            CoachInsightCard(state.insight)
        }
        is AiCascadeState.RateLimited -> Unit
        AiCascadeState.AllProvidersFailed -> {
            Text(
                text = "Coach summary could not be generated.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        AiCascadeState.Offline -> {
            Text(
                text = "Coach summary is unavailable while offline.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CoachInsightCard(insight: CoachInsight) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Coach Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = insight.summaryText,
                style = MaterialTheme.typography.bodyMedium
            )
            if (insight.weaknesses.isNotEmpty()) {
                InsightListBlock(
                    title = "Needs Work",
                    items = insight.weaknesses
                )
            }
            if (insight.strengths.isNotEmpty()) {
                InsightListBlock(
                    title = "What Held Up",
                    items = insight.strengths
                )
            }
            if (insight.youtubeLinks.isNotEmpty()) {
                InsightListBlock(
                    title = "Practice Next",
                    items = insight.youtubeLinks.map { "${it.title}: ${it.searchQuery}" }
                )
            }
        }
    }
}

@Composable
private fun InsightListBlock(
    title: String,
    items: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        items.forEach { item ->
            Text(
                text = "\u2022 $item",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Game History List (Scaffold) ───────────────────────────────────────────────

@Composable
private fun GameHistoryList(
    games: List<com.chessigma.app.data.local.GameEntity>,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Recent Games",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (games.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No games played yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(games.size) { index ->
                    val game = games[index]
                    GameHistoryCard(
                        game = game,
                        onClick = { onGameClick(game.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GameHistoryCard(
    game: com.chessigma.app.data.local.GameEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${game.whiteName} vs ${game.blackName}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${game.result} • ${formatDate(game.datePlayed)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (game.isAnalysed) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccuracyChip("W", game.accuracyWhite?.toInt() ?: 0, Color.White)
                    AccuracyChip("B", game.accuracyBlack?.toInt() ?: 0, Color(0xFF666666))
                }
            } else {
                Text(
                    "Not Analysed",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
