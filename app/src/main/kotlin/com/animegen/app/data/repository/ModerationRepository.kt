package com.animegen.app.data.repository

import com.animegen.app.data.remote.CloudService
import com.animegen.app.data.remote.DalleService
import com.animegen.app.data.remote.ModerationRequest
import com.animegen.app.data.remote.ModerationVerificationRequest
import com.animegen.app.util.ContentFilter
import com.animegen.app.util.ContentModerationException
import com.animegen.app.util.KeyManager
import com.animegen.app.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModerationRepository @Inject constructor(
    private val dalleService: DalleService,
    private val cloudService: CloudService,
    private val keyManager: KeyManager,
    private val contentFilter: ContentFilter
) {

    /**
     * Perform on-device moderation (Layer 1)
     */
    fun performOnDeviceModeration(prompt: String): Result<Unit> {
        return try {
            if (!contentFilter.isContentAppropriate(prompt)) {
                val reason = contentFilter.getViolationReason(prompt)
                    ?: "Content does not meet TV-14 rating requirements"
                Result.Error(
                    ContentModerationException("Moderation failed", reason),
                    reason
                )
            } else {
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(e, "Error during on-device moderation: ${e.message}")
        }
    }

    /**
     * Perform server-side moderation verification (Layer 2)
     * Requires cloud authentication
     */
    suspend fun performServerModeration(
        prompt: String,
        imageUrl: String? = null
    ): Result<Boolean> {
        return try {
            val cloudToken = keyManager.getCloudAuthToken()
            val userId = keyManager.getUserId()

            if (cloudToken == null || userId == null) {
                // Server moderation is optional - return success if no cloud auth
                return Result.Success(true)
            }

            val request = ModerationVerificationRequest(
                prompt = prompt,
                imageUrl = imageUrl,
                userId = userId
            )

            val response = cloudService.verifyContent(
                request,
                "Bearer $cloudToken"
            )

            if (response.approved) {
                Result.Success(true)
            } else {
                Result.Error(
                    ContentModerationException(
                        "Server moderation rejected content",
                        response.reason ?: "Content violates policies"
                    ),
                    response.reason ?: "Content not approved"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Server moderation error: ${e.message}")
        }
    }

    /**
     * Use OpenAI Moderation API for additional verification
     */
    suspend fun performOpenAIModeration(prompt: String): Result<Boolean> {
        return try {
            val apiKey = keyManager.getDalleApiKey()
                ?: return Result.Success(true) // Skip if no API key

            val request = ModerationRequest(input = prompt)
            val response = dalleService.moderateContent(
                request,
                "Bearer $apiKey"
            )

            val isSafe = response.results.all { !it.flagged }

            if (isSafe) {
                Result.Success(true)
            } else {
                val violations = response.results
                    .filter { it.flagged }
                    .flatMap { result ->
                        listOfNotNull(
                            if (result.categories.sexual) "sexual content",
                            if (result.categories.hate) "hate speech",
                            if (result.categories.harassment) "harassment",
                            if (result.categories.selfHarm) "self-harm",
                            if (result.categories.sexualMinors) "sexual content with minors",
                            if (result.categories.hateThreatening) "hate speech with threats",
                            if (result.categories.violenceGraphic) "graphic violence",
                            if (result.categories.violence) "violence"
                        )
                    }
                    .distinct()

                Result.Error(
                    ContentModerationException(
                        "OpenAI moderation rejected content",
                        "Contains: ${violations.joinToString(", ")}"
                    ),
                    violations.joinToString(", ")
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "OpenAI moderation error: ${e.message}")
        }
    }

    /**
     * Comprehensive moderation check combining both layers
     */
    suspend fun performFullModeration(
        prompt: String,
        imageUrl: String? = null
    ): Result<Unit> {
        // Layer 1: On-device moderation
        val onDeviceResult = performOnDeviceModeration(prompt)
        if (onDeviceResult.isError()) {
            return Result.Error(
                (onDeviceResult as Result.Error).exception,
                onDeviceResult.message
            )
        }

        // Layer 2: OpenAI Moderation API
        val openAIResult = performOpenAIModeration(prompt)
        if (openAIResult.isError()) {
            return Result.Error(
                (openAIResult as Result.Error).exception,
                openAIResult.message
            )
        }

        // Layer 3: Server-side verification (if authenticated)
        val serverResult = performServerModeration(prompt, imageUrl)
        if (serverResult.isError()) {
            return Result.Error(
                (serverResult as Result.Error).exception,
                serverResult.message
            )
        }

        return Result.Success(Unit)
    }
}
