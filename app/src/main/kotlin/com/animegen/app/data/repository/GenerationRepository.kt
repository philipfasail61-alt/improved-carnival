package com.animegen.app.data.repository

import com.animegen.app.data.db.AppDatabase
import com.animegen.app.data.db.GenerationHistoryEntity
import com.animegen.app.data.db.ImageEntity
import com.animegen.app.data.remote.DalleImageRequest
import com.animegen.app.data.remote.DalleService
import com.animegen.app.util.ApiKeyNotSetException
import com.animegen.app.util.ContentFilter
import com.animegen.app.util.ContentModerationException
import com.animegen.app.util.DalleApiException
import com.animegen.app.util.KeyManager
import com.animegen.app.util.NetworkException
import com.animegen.app.util.Result
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerationRepository @Inject constructor(
    private val dalleService: DalleService,
    private val keyManager: KeyManager,
    private val database: AppDatabase,
    private val contentFilter: ContentFilter
) {

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L
    }

    suspend fun generateAnimeImage(prompt: String): Result<String> {
        return try {
            // Check if API key is configured
            val apiKey = keyManager.getDalleApiKey()
                ?: return Result.Error(
                    ApiKeyNotSetException(),
                    "DALL-E API key not configured. Please set it in settings."
                )

            // Perform on-device content moderation
            if (!contentFilter.isContentAppropriate(prompt)) {
                val reason = contentFilter.getViolationReason(prompt)
                    ?: "Content does not meet TV-14 rating requirements"
                return Result.Error(
                    ContentModerationException(
                        "Content moderation failed",
                        reason
                    ),
                    reason
                )
            }

            // Save generation history
            val historyEntity = GenerationHistoryEntity(
                prompt = prompt,
                status = "pending"
            )
            val historyId = database.generationHistoryDao().insertHistory(historyEntity)

            // Call DALL-E API with retry logic
            val imageUrl = callDalleWithRetry(apiKey, prompt)

            // Download and save image locally
            val imagePath = downloadAndSaveImage(imageUrl, prompt)

            // Create image entity
            val imageEntity = ImageEntity(
                prompt = prompt,
                imagePath = imagePath,
                timestamp = System.currentTimeMillis()
            )
            database.imageDao().insertImage(imageEntity)

            // Update generation history as successful
            database.generationHistoryDao().updateHistory(
                historyEntity.copy(
                    id = historyId,
                    status = "success"
                )
            )

            Result.Success(imagePath)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Unknown error during image generation")
        }
    }

    private suspend fun callDalleWithRetry(apiKey: String, prompt: String): String {
        var lastException: Exception? = null
        var retryDelay = INITIAL_RETRY_DELAY_MS

        repeat(MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                val request = DalleImageRequest(prompt = prompt)
                val response = dalleService.generateImage(
                    request,
                    "Bearer $apiKey"
                )

                if (response.data.isNotEmpty()) {
                    return response.data[0].url
                } else {
                    throw DalleApiException("No image data returned from DALL-E")
                }
            } catch (e: Exception) {
                lastException = e
                if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                    delay(retryDelay)
                    retryDelay *= 2
                }
            }
        }

        throw lastException ?: DalleApiException("Failed to generate image after retries")
    }

    private suspend fun downloadAndSaveImage(imageUrl: String, prompt: String): String {
        // TODO: Implement image download and local storage
        // For now, return the URL as placeholder
        return imageUrl
    }

    fun getGenerationHistory() = database.generationHistoryDao().getAllHistory()

    fun getFailedGenerations() = database.generationHistoryDao()
        .getHistoryByStatus("failed")
}
