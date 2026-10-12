package com.animegen.app.data.repository

import com.animegen.app.util.ApiKeyNotValidException
import com.animegen.app.util.KeyManager
import com.animegen.app.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val keyManager: KeyManager
) {

    fun saveDalleApiKey(apiKey: String): Result<Unit> {
        return try {
            if (!keyManager.isApiKeyValid(apiKey)) {
                return Result.Error(
                    ApiKeyNotValidException("API key must start with 'sk-'"),
                    "Invalid API key format"
                )
            }

            keyManager.saveDalleApiKey(apiKey)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Failed to save API key: ${e.message}")
        }
    }

    fun getDalleApiKey(): String? {
        return keyManager.getDalleApiKey()
    }

    fun hasDalleApiKey(): Boolean {
        return keyManager.hasDalleApiKey()
    }

    fun deleteDalleApiKey(): Result<Unit> {
        return try {
            keyManager.deleteDalleApiKey()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Failed to delete API key: ${e.message}")
        }
    }

    fun validateApiKey(apiKey: String): Boolean {
        return keyManager.isApiKeyValid(apiKey)
    }

    fun clearAllAuth(): Result<Unit> {
        return try {
            keyManager.clearAllKeys()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Failed to clear authentication: ${e.message}")
        }
    }
}
