package com.chessigma.app.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chessigma.app.domain.model.EloPoint
import com.chessigma.app.ui.util.screenEntryTransition
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val eloHistory by viewModel.eloHistory.collectAsState()
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }

    val displayHistory = if (eloHistory.isEmpty()) {
        mockEloHistory()
    } else {
        eloHistory
    }

    screenEntryTransition(visible = visible) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = { Text("Statistics", fontWeight = FontWeight.Bold) }
                )
            },
            modifier = modifier
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                EloTrendCard(displayHistory)
                
                // Accuracy Card (Placeholder for now)
                AccuracyCard()
            }
        }
    }
}

@Composable
fun EloTrendCard(history: List<EloPoint>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ELO Trend",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val chartEntryModel = entryModelOf(*history.map { it.elo.toFloat() }.toTypedArray())
            
            Chart(
                chart = lineChart(
                    lines = listOf(
                        com.patrykandpatrick.vico.compose.chart.line.lineSpec(
                            lineColor = MaterialTheme.colorScheme.primary
                        )
                    )
                ),
                model = chartEntryModel,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Current ELO: ${history.lastOrNull()?.elo ?: 0}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AccuracyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recent Accuracy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mock accuracy stat for scaffold
            LinearProgressIndicator(
                progress = { 0.82f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Avg. 82.5% (Last 10 games)",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun mockEloHistory(): List<EloPoint> {
    val now = System.currentTimeMillis()
    val day = 24 * 60 * 60 * 1000L
    return listOf(
        EloPoint("LOCAL", now - 5 * day, 1200),
        EloPoint("LOCAL", now - 4 * day, 1215),
        EloPoint("LOCAL", now - 3 * day, 1210),
        EloPoint("LOCAL", now - 2 * day, 1230),
        EloPoint("LOCAL", now - 1 * day, 1245),
        EloPoint("LOCAL", now, 1240)
    )
}
