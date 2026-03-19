package com.chessigma.app.ui.puzzles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chessigma.app.data.local.PersonalPuzzleWithGame
import com.chessigma.app.ui.puzzles.viewmodel.PuzzleViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PuzzleScreen(
    viewModel: PuzzleViewModel = hiltViewModel()
) {
    val personalPuzzles by viewModel.personalPuzzles.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("My Mistakes", "Daily Puzzles")

    Column(modifier = Modifier.fillMaxSize()) {
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
            0 -> MyMistakesTabContent(personalPuzzles)
            1 -> DailyPuzzlesTabContent()
        }
    }
}

@Composable
fun MyMistakesTabContent(puzzles: List<PersonalPuzzleWithGame>) {
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(puzzles) { item ->
            val puzzle = item.puzzle
            val game = item.game

            val opponent = if (game.userColor == "WHITE") game.blackName else game.whiteName
            val date = dateFormatter.format(Date(game.datePlayed))
            val blunderSan = puzzle.blunderSan

            Column {
                Text(
                    text = "From your game vs $opponent on $date — you played $blunderSan",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // TODO: ChessBoardView stub here
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Text("Chessboard placeholder for ${puzzle.fenPosition}")
                }
            }
        }
    }
}

@Composable
fun DailyPuzzlesTabContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Daily Puzzles based on ELO +/- 200")
        // TODO: Fetch from BundledPuzzleEntity
    }
}
