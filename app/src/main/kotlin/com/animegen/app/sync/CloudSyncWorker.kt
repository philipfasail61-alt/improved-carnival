package com.animegen.app.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Background sync worker for periodic image synchronization
 */
class CloudSyncWorker(
    context: Context,
    params: androidx.work.WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // TODO: Inject CloudSyncRepository to perform actual sync
            // This placeholder ensures syncing framework is in place
            Result.success()
        } catch (e: Exception) {
            // Retry on any exception
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "cloud_sync_work"
    }
}

/**
 * Manages background sync scheduling and monitoring
 * Handles periodic sync configuration with WorkManager
 */
@Singleton
class SyncScheduler @Inject constructor(
    private val context: Context
) {

    /**
     * Schedule periodic image synchronization
     * Runs every 15 minutes when appropriate conditions are met
     */
    fun schedulePeriodicSync() {
        val syncRequest = PeriodicWorkRequestBuilder<CloudSyncWorker>(
            15, TimeUnit.MINUTES
        )
            .addTag("cloud_sync")
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "cloud_sync_work",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    /**
     * Cancel all scheduled periodic syncs
     */
    fun cancelPeriodicSync() {
        WorkManager.getInstance(context).cancelAllWorkByTag("cloud_sync")
    }

    /**
     * Trigger immediate one-time sync
     * Useful for manual sync button or app initialization
     */
    fun forceImmediateSync() {
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<CloudSyncWorker>()
            .addTag("cloud_sync_immediate")
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }

    /**
     * Check if sync is currently active or scheduled
     */
    fun isSyncScheduled(): Boolean {
        return try {
            val workInfos = WorkManager.getInstance(context)
                .getWorkInfosByTag("cloud_sync")
                .get()

            workInfos.any { it.state == androidx.work.WorkInfo.State.ENQUEUED }
        } catch (e: Exception) {
            false
        }
    }
}
