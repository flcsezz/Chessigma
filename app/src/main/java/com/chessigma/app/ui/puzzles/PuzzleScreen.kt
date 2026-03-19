package com.chessigma.app.ui.puzzles

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PuzzleScreen() {
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
            0 -> MyMistakesTabContent()
            1 -> DailyPuzzlesTabContent()
        }
    }
}

@Composable
fun MyMistakesTabContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        // TODO: Map over actual data from PersonalPuzzleEntity rows
        val opponent = "MagnusCarlsen" // stub
        val date = "2023-10-25" // stub
        val blunderSan = "Nxf7" // stub
        
        Text(
            text = "From your game vs $opponent on $date — you played $blunderSan",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // TODO: ChessBoardView stub here
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            Text("Chessboard placeholder")
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
