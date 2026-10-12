package com.animegen.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.animegen.app.viewmodel.CloudSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Cloud sync status and control panel
 */
@Composable
fun SyncPanel(
    viewModel: CloudSyncViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        // Header
        Text(
            "Cloud Synchronization",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Sync Status Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Status header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudSync,
                        contentDescription = "Sync status",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        uiState.syncStatus,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    if (uiState.isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Last sync time
                if (uiState.lastSyncTime != null) {
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    val lastSyncDate = dateFormat.format(Date(uiState.lastSyncTime))
                    Text(
                        "Last sync: $lastSyncDate",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Sync stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            "${uiState.syncedImageCount}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            "Synced",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Column {
                        Text(
                            "${uiState.pendingImageCount}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            "Pending",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Auto-sync toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Auto-sync",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = uiState.autoSyncEnabled,
                        onCheckedChange = { viewModel.toggleAutoSync(it) },
                        enabled = !uiState.isSyncing
                    )
                }
            }
        }

        // Error message
        if (uiState.errorMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        uiState.errorMessage!!,
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(
                        onClick = { viewModel.clearError() },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }

        // Action buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.performSync() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                enabled = !uiState.isSyncing
            ) {
                if (uiState.isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Sync Now")
            }

            OutlinedButton(
                onClick = { viewModel.forceImmediateSync() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                enabled = !uiState.isSyncing
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Force sync",
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 8.dp)
                )
                Text("Force Sync")
            }
        }
    }
}
