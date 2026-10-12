package com.animegen.app.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DalleService {
    @POST("images/generations")
    suspend fun generateImage(
        @Body request: DalleImageRequest,
        @Header("Authorization") authHeader: String
    ): DalleImageResponse

    @POST("moderations")
    suspend fun moderateContent(
        @Body request: ModerationRequest,
        @Header("Authorization") authHeader: String
    ): ModerationResponse
}
