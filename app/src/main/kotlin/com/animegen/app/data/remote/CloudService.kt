package com.animegen.app.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CloudService {
    @POST("auth/login")
    suspend fun login(
        @Body request: CloudAuthRequest
    ): CloudAuthResponse

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: CloudRefreshTokenRequest
    ): CloudRefreshTokenResponse

    @POST("moderation/verify")
    suspend fun verifyContent(
        @Body request: ModerationVerificationRequest,
        @Header("Authorization") authHeader: String
    ): ModerationVerificationResponse

    @POST("sync/upload")
    suspend fun syncImages(
        @Body request: SyncRequest,
        @Header("Authorization") authHeader: String
    ): SyncResponse
}
