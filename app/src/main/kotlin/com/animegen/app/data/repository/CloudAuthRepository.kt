package com.animegen.app.data.repository

import com.animegen.app.data.remote.CloudAuthRequest
import com.animegen.app.data.remote.CloudRefreshTokenRequest
import com.animegen.app.data.remote.CloudService
import com.animegen.app.util.AuthenticationException
import com.animegen.app.util.KeyManager
import com.animegen.app.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudAuthRepository @Inject constructor(
    private val cloudService: CloudService,
    private val keyManager: KeyManager
) {

    /**
     * Login with email and password
     */
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return Result.Error(
                    AuthenticationException("Email and password are required"),
                    "Please provide both email and password"
                )
            }

            val request = CloudAuthRequest(
                email = email,
                password = password
            )

            val response = cloudService.login(request)

            // Save tokens locally
            keyManager.saveCloudAuthToken(response.token)
            keyManager.saveUserId(response.userId)

            Result.Success(response.userId)
        } catch (e: Exception) {
            Result.Error(e, "Login failed: ${e.message}")
        }
    }

    /**
     * Register (placeholder - implement backend endpoint)
     */
    suspend fun register(email: String, password: String): Result<String> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return Result.Error(
                    AuthenticationException("Email and password are required"),
                    "Please provide both email and password"
                )
            }

            // TODO: Implement backend registration endpoint
            // For now, return a placeholder
            Result.Error(
                AuthenticationException("Registration not yet implemented"),
                "Registration coming soon"
            )
        } catch (e: Exception) {
            Result.Error(e, "Registration failed: ${e.message}")
        }
    }

    /**
     * Logout by clearing tokens
     */
    fun logout(): Result<Unit> {
        return try {
            keyManager.deleteCloudAuthToken()
            keyManager.deleteUserId()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Logout failed: ${e.message}")
        }
    }

    /**
     * Refresh authentication token
     */
    suspend fun refreshToken(currentToken: String): Result<String> {
        return try {
            val request = CloudRefreshTokenRequest(refreshToken = currentToken)
            val response = cloudService.refreshToken(request)

            keyManager.saveCloudAuthToken(response.token)

            Result.Success(response.token)
        } catch (e: Exception) {
            Result.Error(e, "Token refresh failed: ${e.message}")
        }
    }

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return keyManager.getCloudAuthToken() != null && keyManager.getUserId() != null
    }

    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String? {
        return keyManager.getUserId()
    }

    /**
     * Get current auth token
     */
    fun getCurrentToken(): String? {
        return keyManager.getCloudAuthToken()
    }

    /**
     * Clear all authentication data
     */
    fun clearAuth(): Result<Unit> {
        return try {
            keyManager.deleteCloudAuthToken()
            keyManager.deleteUserId()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Clear auth failed: ${e.message}")
        }
    }
}
