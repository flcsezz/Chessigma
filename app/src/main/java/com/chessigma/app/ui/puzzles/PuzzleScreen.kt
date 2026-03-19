package com.chessigma.app.ui.puzzles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chessigma.app.data.local.PersonalPuzzleWithGame
import com.chessigma.app.domain.model.ChessBoard
import com.chessigma.app.domain.model.PieceColor
import com.chessigma.app.domain.model.PieceType
import com.chessigma.app.ui.components.Chessboard
import com.chessigma.app.ui.components.rememberChessboardState
import com.chessigma.app.ui.puzzles.viewmodel.PuzzlePlayViewModel
import com.chessigma.app.ui.puzzles.viewmodel.PuzzleUiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PuzzleScreen(
    viewModel: PuzzlePlayViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("My Mistakes", "Daily Puzzles")

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> MyMistakesTabContent(
                uiState = uiState,
                onSquareClick = viewModel::onSquareClick,
                onNextPuzzle = viewModel::nextPuzzle,
                onPreviousPuzzle = viewModel::previousPuzzle,
                onDismissResult = viewModel::dismissResult
            )
            1 -> DailyPuzzlesTabContent()
        }
    }
}

@Composable
fun MyMistakesTabContent(
    uiState: PuzzleUiState,
    onSquareClick: (String) -> Unit,
    onNextPuzzle: () -> Unit,
    onPreviousPuzzle: () -> Unit,
    onDismissResult: () -> Unit
) {
    val boardState = rememberChessboardState()

    LaunchedEffect(uiState.board, uiState.selectedSquare, uiState.lastMove) {
        uiState.board?.let { board ->
            boardState.board = board
            boardState.legalMoves = uiState.legalMoves
            boardState.lastMove = uiState.lastMove
            boardState.selectedSquare = uiState.selectedSquare
        }
    }

    if (uiState.puzzles.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No puzzles yet",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Review a game to generate puzzles from your mistakes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Puzzle ${uiState.currentPuzzleIndex + 1} of ${uiState.puzzles.size}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Solved: ${uiState.solvedCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        uiState.currentPuzzle?.let { puzzle ->
            val classification = puzzle.originalClassification
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when (classification) {
                        "BLUNDER" -> MaterialTheme.colorScheme.errorContainer
                        "MISTAKE" -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            ) {
                Text(
                    text = "You played ${puzzle.blunderSan}, find the best move",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Chessboard(
            state = boardState,
            onMove = {},
            onSquareClick = onSquareClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = onPreviousPuzzle,
                enabled = uiState.currentPuzzleIndex > 0
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
            }

            Text(
                text = "Attempts: ${uiState.attemptCount}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            IconButton(
                onClick = onNextPuzzle,
                enabled = uiState.currentPuzzleIndex < uiState.puzzles.size - 1
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }

        if (uiState.showResult) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isCorrect == true)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.isCorrect == true)
                                Icons.Default.Check
                            else
                                Icons.Default.Close,
                            contentDescription = null,
                            tint = if (uiState.isCorrect == true)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isCorrect == true)
                                "Correct! Well done!"
                            else
                                "Not quite. Try again!",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyPuzzlesTabContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Daily Puzzles based on ELO +/- 200")
    }
}
