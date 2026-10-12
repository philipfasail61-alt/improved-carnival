package com.animegen.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DALL-E 2 Image Generation Request
@Serializable
data class DalleImageRequest(
    val prompt: String,
    val n: Int = 1,
    val size: String = "512x512",
    val response_format: String = "url",
    @SerialName("user_id")
    val userId: String? = null
)

// DALL-E 2 Image Generation Response
@Serializable
data class DalleImageResponse(
    val created: Long,
    val data: List<DalleImage>
)

@Serializable
data class DalleImage(
    val url: String,
    @SerialName("revised_prompt")
    val revisedPrompt: String? = null
)

// Error response from DALL-E
@Serializable
data class DalleErrorResponse(
    val error: DalleError
)

@Serializable
data class DalleError(
    val message: String,
    val type: String? = null,
    val param: String? = null,
    val code: String? = null
)

// Moderation API Request
@Serializable
data class ModerationRequest(
    val input: String
)

// Moderation API Response
@Serializable
data class ModerationResponse(
    val id: String,
    val model: String,
    val results: List<ModerationResult>
)

@Serializable
data class ModerationResult(
    val flagged: Boolean,
    val categories: ModerationCategories,
    @SerialName("category_scores")
    val categoryScores: ModerationScores
)

@Serializable
data class ModerationCategories(
    val sexual: Boolean,
    val hate: Boolean,
    @SerialName("harassment")
    val harassment: Boolean,
    @SerialName("self-harm")
    val selfHarm: Boolean,
    @SerialName("sexual/minors")
    val sexualMinors: Boolean,
    @SerialName("hate/threatening")
    val hateThreatening: Boolean,
    @SerialName("violence/graphic")
    val violenceGraphic: Boolean,
    val violence: Boolean
)

@Serializable
data class ModerationScores(
    val sexual: Double,
    val hate: Double,
    val harassment: Double,
    @SerialName("self-harm")
    val selfHarm: Double,
    @SerialName("sexual/minors")
    val sexualMinors: Double,
    @SerialName("hate/threatening")
    val hateThreatening: Double,
    @SerialName("violence/graphic")
    val violenceGraphic: Double,
    val violence: Double
)
