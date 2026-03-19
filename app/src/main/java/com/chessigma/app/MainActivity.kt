package com.chessigma.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.chessigma.app.ui.play.PlayRoute
import com.chessigma.app.ui.puzzles.PuzzleScreen
import com.chessigma.app.ui.review.ReviewRoute
import com.chessigma.app.ui.theme.ChessigmaTheme
import dagger.hilt.android.AndroidEntryPoint

private enum class Tab { PLAY, REVIEW, PUZZLE }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            ChessigmaTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
private fun MainNavigation() {
    var selectedTab by remember { mutableStateOf(Tab.PLAY) }
    var requestedReviewGameId by remember { mutableStateOf<String?>(null) }
    var reviewRequestNonce by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == Tab.PLAY,
                    onClick = { selectedTab = Tab.PLAY },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Play") },
                    label = { Text("Play") }
                )
                NavigationBarItem(
                    selected = selectedTab == Tab.REVIEW,
                    onClick = { selectedTab = Tab.REVIEW },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Review") },
                    label = { Text("Review") }
                )
                NavigationBarItem(
                    selected = selectedTab == Tab.PUZZLE,
                    onClick = { selectedTab = Tab.PUZZLE },
                    icon = { Icon(Icons.Default.List, contentDescription = "Puzzles") },
                    label = { Text("Puzzles") }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            Tab.PLAY   -> PlayRoute(
                onReviewGame = { gameId ->
                    requestedReviewGameId = gameId
                    reviewRequestNonce += 1
                    selectedTab = Tab.REVIEW
                },
                modifier = Modifier.padding(innerPadding)
            )
            Tab.REVIEW -> ReviewRoute(
                requestedGameId = requestedReviewGameId,
                requestNonce = reviewRequestNonce,
                modifier = Modifier.padding(innerPadding)
            )
            Tab.PUZZLE -> PuzzleScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
