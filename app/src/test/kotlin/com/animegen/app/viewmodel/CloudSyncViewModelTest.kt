package com.animegen.app.viewmodel

import com.animegen.app.data.remote.CloudModels
import com.animegen.app.data.repository.CloudSyncRepository
import com.animegen.app.sync.SyncScheduler
import com.animegen.app.util.Result
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CloudSyncViewModelTest {

    @Mock
    private lateinit var cloudSyncRepository: CloudSyncRepository

    @Mock
    private lateinit var syncScheduler: SyncScheduler

    private lateinit var viewModel: CloudSyncViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testInitialSyncStatus() = runTest {
        // Given
        val mockStatus = CloudModels.SyncStatus(
            lastSyncTime = System.currentTimeMillis(),
            statusMessage = "Synced",
            syncedImageCount = 5,
            pendingImageCount = 2
        )
        whenever(cloudSyncRepository.getSyncStatus()).thenReturn(mockStatus)
        whenever(syncScheduler.isSyncScheduled()).thenReturn(false)

        // When
        viewModel = CloudSyncViewModel(cloudSyncRepository, syncScheduler)

        // Then
        assertEquals("Synced", viewModel.uiState.value.syncStatus)
        assertEquals(5, viewModel.uiState.value.syncedImageCount)
        assertEquals(2, viewModel.uiState.value.pendingImageCount)
    }

    @Test
    fun testToggleAutoSync() {
        // Given
        viewModel = CloudSyncViewModel(cloudSyncRepository, syncScheduler)

        // When
        viewModel.toggleAutoSync(true)

        // Then
        assertEquals(true, viewModel.uiState.value.autoSyncEnabled)
    }

    @Test
    fun testClearError() {
        // Given
        viewModel = CloudSyncViewModel(cloudSyncRepository, syncScheduler)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
