package com.animegen.app.util

import androidx.security.crypto.EncryptedSharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyManager @Inject constructor(
    private val encryptedPrefs: EncryptedSharedPreferences
) {

    companion object {
        private const val DALLE_API_KEY = "dalle_api_key"
        private const val LOCAL_AUTH_TOKEN = "local_auth_token"
        private const val CLOUD_AUTH_TOKEN = "cloud_auth_token"
        private const val USER_ID = "user_id"
    }

    fun saveDalleApiKey(apiKey: String) {
        encryptedPrefs.edit().putString(DALLE_API_KEY, apiKey).apply()
    }

    fun getDalleApiKey(): String? {
        return encryptedPrefs.getString(DALLE_API_KEY, null)
    }

    fun deleteDalleApiKey() {
        encryptedPrefs.edit().remove(DALLE_API_KEY).apply()
    }

    fun hasDalleApiKey(): Boolean {
        return encryptedPrefs.contains(DALLE_API_KEY)
    }

    fun saveLocalAuthToken(token: String) {
        encryptedPrefs.edit().putString(LOCAL_AUTH_TOKEN, token).apply()
    }

    fun getLocalAuthToken(): String? {
        return encryptedPrefs.getString(LOCAL_AUTH_TOKEN, null)
    }

    fun deleteLocalAuthToken() {
        encryptedPrefs.edit().remove(LOCAL_AUTH_TOKEN).apply()
    }

    fun saveCloudAuthToken(token: String) {
        encryptedPrefs.edit().putString(CLOUD_AUTH_TOKEN, token).apply()
    }

    fun getCloudAuthToken(): String? {
        return encryptedPrefs.getString(CLOUD_AUTH_TOKEN, null)
    }

    fun deleteCloudAuthToken() {
        encryptedPrefs.edit().remove(CLOUD_AUTH_TOKEN).apply()
    }

    fun saveUserId(userId: String) {
        encryptedPrefs.edit().putString(USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return encryptedPrefs.getString(USER_ID, null)
    }

    fun deleteUserId() {
        encryptedPrefs.edit().remove(USER_ID).apply()
    }

    fun clearAllKeys() {
        encryptedPrefs.edit().clear().apply()
    }

    fun isApiKeyValid(apiKey: String): Boolean {
        return apiKey.isNotBlank() && apiKey.startsWith("sk-")
    }
}
