package com.animegen.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.animegen.app.viewmodel.GenerationViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: GenerationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Header
        Text(
            "Anime Generator",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Text(
            "Create stunning anime characters with AI",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // API Key reminder
        if (!uiState.hasApiKey) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No API Key Configured",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Please configure your DALL-E 2 API key in settings",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Button(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Go to Settings")
                    }
                }
            }
        } else {
            // Prompt input
            OutlinedTextField(
                value = uiState.prompt,
                onValueChange = { viewModel.updatePrompt(it) },
                label = { Text("Describe your anime character...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                minLines = 3,
                maxLines = 5,
                enabled = !uiState.isLoading
            )

            // Error message
            if (uiState.error != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            uiState.error!!,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Button(
                            onClick = { viewModel.clearError() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            // Generated image display
            if (uiState.generatedImageUrl != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Image URL:", style = MaterialTheme.typography.bodySmall)
                        Text(
                            uiState.generatedImageUrl!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Generate button
            Button(
                onClick = { viewModel.generateImage() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !uiState.isLoading && uiState.prompt.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Generate Image")
                }
            }
        }

        // Bottom navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.navigate("gallery") },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("Gallery")
            }
            Button(
                onClick = { navController.navigate("settings") },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("Settings")
            }
        }
    }
}
