package com.chessigma.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.chessigma.app.ui.play.PlayRoute
import com.chessigma.app.ui.puzzles.PuzzleScreen
import com.chessigma.app.ui.review.ReviewRoute
import com.chessigma.app.ui.settings.SettingsScreen
import com.chessigma.app.ui.stats.StatsScreen
import com.chessigma.app.ui.theme.ChessigmaTheme
import dagger.hilt.android.AndroidEntryPoint

private enum class Tab { PLAY, REVIEW, PUZZLE, STATS, SETTINGS }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            ChessigmaTheme {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    visible = true
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = visible,
                    enter = androidx.compose.animation.fadeIn(animationSpec = tween(1000)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainNavigation()
                }
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
            FloatingNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (selectedTab) {
            Tab.PLAY -> PlayRoute(
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
            Tab.STATS -> StatsScreen(
                modifier = Modifier.padding(innerPadding)
            )
            Tab.SETTINGS -> SettingsScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun FloatingNavigationBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(32.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarItem(
                    icon = Icons.Default.PlayArrow,
                    label = "Play",
                    selected = selectedTab == Tab.PLAY,
                    onClick = { onTabSelected(Tab.PLAY) }
                )
                NavBarItem(
                    icon = Icons.Default.Search,
                    label = "Review",
                    selected = selectedTab == Tab.REVIEW,
                    onClick = { onTabSelected(Tab.REVIEW) }
                )
                NavBarItem(
                    icon = Icons.AutoMirrored.Filled.List,
                    label = "Puzzles",
                    selected = selectedTab == Tab.PUZZLE,
                    onClick = { onTabSelected(Tab.PUZZLE) }
                )
                NavBarItem(
                    icon = Icons.Default.DateRange,
                    label = "Stats",
                    selected = selectedTab == Tab.STATS,
                    onClick = { onTabSelected(Tab.STATS) }
                )
                NavBarItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    selected = selectedTab == Tab.SETTINGS,
                    onClick = { onTabSelected(Tab.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material.ripple.rememberRipple(), // Ensure material ripple or null requires proper cast
                onClick = onClick
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (selected) primaryColor.copy(alpha = 0.15f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) primaryColor else onSurfaceColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        AnimatedVisibility(
            visible = selected,
            enter = slideInVertically(animationSpec = tween(300)) { it / 2 },
            exit = slideOutVertically(animationSpec = tween(200)) { it / 2 }
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = primaryColor
            )
        }
    }
}
