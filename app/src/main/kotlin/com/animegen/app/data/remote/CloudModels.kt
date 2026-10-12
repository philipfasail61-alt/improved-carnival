package com.animegen.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Backend verification models
@Serializable
data class ModerationVerificationRequest(
    val prompt: String,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("user_id")
    val userId: String? = null
)

@Serializable
data class ModerationVerificationResponse(
    val approved: Boolean,
    val reason: String? = null,
    @SerialName("violation_type")
    val violationType: String? = null,
    @SerialName("severity_score")
    val severityScore: Double? = null
)

@Serializable
data class SyncRequest(
    val images: List<SyncImageData>,
    @SerialName("user_id")
    val userId: String
)

@Serializable
data class SyncImageData(
    val prompt: String,
    @SerialName("image_url")
    val imageUrl: String,
    val timestamp: Long,
    val tags: String? = null
)

@Serializable
data class SyncResponse(
    @SerialName("synced_count")
    val syncedCount: Int,
    @SerialName("failed_count")
    val failedCount: Int,
    val errors: List<SyncError>? = null
)

@Serializable
data class SyncError(
    val prompt: String,
    val message: String
)

@Serializable
data class CloudAuthRequest(
    val email: String,
    val password: String
)

@Serializable
data class CloudAuthResponse(
    val token: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("expires_in")
    val expiresIn: Long
)

@Serializable
data class CloudRefreshTokenRequest(
    @SerialName("refresh_token")
    val refreshToken: String
)

@Serializable
data class CloudRefreshTokenResponse(
    val token: String,
    @SerialName("expires_in")
    val expiresIn: Long
)
