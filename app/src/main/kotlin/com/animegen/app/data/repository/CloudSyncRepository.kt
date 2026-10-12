package com.animegen.app.data.repository

import com.animegen.app.data.db.AppDatabase
import com.animegen.app.data.db.ImageEntity
import com.animegen.app.data.remote.CloudService
import com.animegen.app.data.remote.SyncImageData
import com.animegen.app.data.remote.SyncRequest
import com.animegen.app.util.KeyManager
import com.animegen.app.util.Result
import com.animegen.app.util.SyncException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncRepository @Inject constructor(
    private val cloudService: CloudService,
    private val keyManager: KeyManager,
    private val database: AppDatabase
) {

    /**
     * Upload local gallery to cloud
     */
    suspend fun syncLocalGalleryToCloud(): Result<Unit> {
        return try {
            val cloudToken = keyManager.getCloudAuthToken()
                ?: return Result.Error(
                    SyncException("Not authenticated"),
                    "Please authenticate to sync images"
                )

            val userId = keyManager.getUserId()
                ?: return Result.Error(
                    SyncException("User ID not found"),
                    "Invalid authentication state"
                )

            // Get all unsynced images
            val images = database.imageDao().getAllImages()

            var syncedCount = 0
            images.collect { imageList ->
                val unsyncedImages = imageList.filter { !it.cloudSynced }

                if (unsyncedImages.isNotEmpty()) {
                    val syncData = unsyncedImages.map { image ->
                        SyncImageData(
                            prompt = image.prompt,
                            imageUrl = image.imagePath,
                            timestamp = image.timestamp,
                            tags = image.tags
                        )
                    }

                    val request = SyncRequest(
                        images = syncData,
                        userId = userId
                    )

                    val response = cloudService.syncImages(
                        request,
                        "Bearer $cloudToken"
                    )

                    // Mark synced images
                    unsyncedImages.forEach { image ->
                        if (response.syncedCount > 0) {
                            database.imageDao().updateImage(
                                image.copy(
                                    cloudSynced = true,
                                    cloudId = "cloud_${System.currentTimeMillis()}_${image.id}"
                                )
                            )
                            syncedCount++
                        }
                    }
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Sync failed: ${e.message}")
        }
    }

    /**
     * Download cloud gallery (placeholder)
     */
    suspend fun downloadCloudGallery(): Result<Int> {
        return try {
            val cloudToken = keyManager.getCloudAuthToken()
                ?: return Result.Error(
                    SyncException("Not authenticated"),
                    "Please authenticate to download images"
                )

            // TODO: Implement backend endpoint to fetch user's cloud images
            // For now, return success with 0 downloaded
            Result.Success(0)
        } catch (e: Exception) {
            Result.Error(e, "Download failed: ${e.message}")
        }
    }

    /**
     * Get sync status
     */
    fun getSyncStatus(): Flow<SyncStatus> {
        return kotlinx.coroutines.flow.flow {
            try {
                val allImages = database.imageDao().getAllImages()
                allImages.collect { images ->
                    val total = images.size
                    val synced = images.count { it.cloudSynced }

                    emit(SyncStatus(
                        totalImages = total,
                        syncedImages = synced,
                        pendingImages = total - synced,
                        isAuthenticated = keyManager.getCloudAuthToken() != null
                    ))
                }
            } catch (e: Exception) {
                emit(SyncStatus(error = e.message))
            }
        }
    }

    /**
     * Enable automatic sync (background service)
     */
    fun enableAutoSync(): Result<Unit> {
        return try {
            // TODO: Implement WorkManager for periodic sync
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Failed to enable auto sync")
        }
    }

    /**
     * Disable automatic sync
     */
    fun disableAutoSync(): Result<Unit> {
        return try {
            // TODO: Cancel WorkManager periodic task
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Failed to disable auto sync")
        }
    }
}

data class SyncStatus(
    val totalImages: Int = 0,
    val syncedImages: Int = 0,
    val pendingImages: Int = 0,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val lastSyncTime: Long? = null
)
