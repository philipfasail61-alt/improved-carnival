package com.animegen.app.util

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String = exception.message ?: "Unknown error") : Result<Nothing>()
    data object Loading : Result<Nothing>()

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }

    fun isSuccess() = this is Success
    fun isError() = this is Error
    fun isLoading() = this is Loading
}

// Custom exceptions
sealed class AnimeGenException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

class ApiKeyNotValidException(message: String = "API key is not valid") :
    AnimeGenException(message)

class ApiKeyNotSetException(message: String = "API key is not configured") :
    AnimeGenException(message)

class ContentModerationException(message: String, val violationReason: String) :
    AnimeGenException(message)

class DalleApiException(message: String, val errorCode: String? = null) :
    AnimeGenException(message)

class NetworkException(message: String = "Network error occurred", cause: Throwable? = null) :
    AnimeGenException(message, cause)

class DatabaseException(message: String, cause: Throwable? = null) :
    AnimeGenException(message, cause)

class AuthenticationException(message: String = "Authentication failed") :
    AnimeGenException(message)

class SyncException(message: String = "Synchronization failed", cause: Throwable? = null) :
    AnimeGenException(message, cause)
