package com.animegen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegen.app.data.repository.AuthRepository
import com.animegen.app.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val apiKey: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val hasApiKey: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkApiKeyPresence()
    }

    private fun checkApiKeyPresence() {
        val hasKey = authRepository.hasDalleApiKey()
        _uiState.value = _uiState.value.copy(hasApiKey = hasKey)
    }

    fun updateApiKey(apiKey: String) {
        _uiState.value = _uiState.value.copy(apiKey = apiKey, error = null)
    }

    fun saveApiKey() {
        if (_uiState.value.apiKey.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter an API key"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.saveDalleApiKey(_uiState.value.apiKey)

            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        success = "API key saved successfully",
                        error = null,
                        apiKey = "",
                        hasApiKey = true
                    )
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        success = null
                    )
                }

                is Result.Loading -> {}
            }
        }
    }

    fun deleteApiKey() {
        viewModelScope.launch {
            val result = authRepository.deleteDalleApiKey()

            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        success = "API key deleted",
                        hasApiKey = false,
                        apiKey = ""
                    )
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message
                    )
                }

                is Result.Loading -> {}
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, success = null)
    }
}
