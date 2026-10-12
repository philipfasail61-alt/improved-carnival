package com.animegen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegen.app.data.remote.CloudModels
import com.animegen.app.data.repository.CloudSyncRepository
import com.animegen.app.sync.SyncScheduler
import com.animegen.app.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sync UI state
 */
data class SyncUiState(
    val isSyncing: Boolean = false,
    val lastSyncTime: Long? = null,
    val syncStatus: String = "Never synced",
    val autoSyncEnabled: Boolean = false,
    val errorMessage: String? = null,
    val syncedImageCount: Int = 0,
    val pendingImageCount: Int = 0,
)

/**
 * ViewModel for cloud synchronization UI and logic
 * Manages sync state, scheduling, and user interactions
 */
@HiltViewModel
class CloudSyncViewModel @Inject constructor(
    private val cloudSyncRepository: CloudSyncRepository,
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(SyncUiState())
    val uiState: StateFlow<SyncUiState> = _uiState

    init {
        checkSyncStatus()
    }

    /**
     * Check current sync status
     */
    private fun checkSyncStatus() {
        viewModelScope.launch {
            val status = cloudSyncRepository.getSyncStatus()
            updateSyncStatus(status)
        }
    }

    /**
     * Update UI state based on sync status
     */
    private suspend fun updateSyncStatus(status: CloudModels.SyncStatus) {
        _uiState.value = _uiState.value.copy(
            lastSyncTime = status.lastSyncTime,
            syncStatus = status.statusMessage,
            autoSyncEnabled = syncScheduler.isSyncScheduled(),
            syncedImageCount = status.syncedImageCount,
            pendingImageCount = status.pendingImageCount
        )
    }

    /**
     * Manually trigger sync
     */
    fun performSync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, errorMessage = null)

            try {
                val result = cloudSyncRepository.syncImages()
                when (result) {
                    is Result.Success -> {
                        val status = cloudSyncRepository.getSyncStatus()
                        updateSyncStatus(status)
                        _uiState.value = _uiState.value.copy(
                            isSyncing = false,
                            syncStatus = "Sync completed successfully"
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isSyncing = false,
                            errorMessage = result.exception.message ?: "Sync failed"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    /**
     * Toggle automatic sync scheduling
     */
    fun toggleAutoSync(enabled: Boolean) {
        if (enabled) {
            syncScheduler.schedulePeriodicSync()
        } else {
            syncScheduler.cancelPeriodicSync()
        }

        _uiState.value = _uiState.value.copy(autoSyncEnabled = enabled)
    }

    /**
     * Force immediate sync (highest priority)
     */
    fun forceImmediateSync() {
        viewModelScope.launch {
            syncScheduler.forceImmediateSync()
            performSync()
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Refresh sync status from repository
     */
    fun refreshSyncStatus() {
        viewModelScope.launch {
            checkSyncStatus()
        }
    }
}
