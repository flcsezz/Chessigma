package com.chessigma.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "AI Provider Keys",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            ApiKeyField(
                label = "Gemini API Key",
                value = settings.geminiApiKey,
                onValueChange = viewModel::updateGeminiKey
            )
            
            ApiKeyField(
                label = "Groq API Key",
                value = settings.groqApiKey,
                onValueChange = viewModel::updateGroqKey
            )
            
            ApiKeyField(
                label = "NVIDIA NIM Key",
                value = settings.nvidiaApiKey,
                onValueChange = viewModel::updateNvidiaKey
            )
            
            HorizontalDivider()
            
            Text(
                text = "Coaching Preferences",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            ProviderDropdown(
                selected = settings.preferredAiProvider,
                onSelected = viewModel::setPreferredProvider
            )
            
            HorizontalDivider()
            
            // About Section
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chessigma v1.0.0 (Phase 5 Polish)")
            }
        }
    }
}

@Composable
fun ApiKeyField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var text by remember(value) { mutableStateOf(value) }
    
    OutlinedTextField(
        value = text,
        onValueChange = { 
            text = it
            onValueChange(it) 
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true
    )
}

@Composable
fun ProviderDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val providers = listOf("GEMINI", "GROQ", "NVIDIA")

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Preferred AI: $selected")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            providers.forEach { provider ->
                DropdownMenuItem(
                    text = { Text(provider) },
                    onClick = {
                        onSelected(provider)
                        expanded = false
                    }
                )
            }
        }
    }
}
