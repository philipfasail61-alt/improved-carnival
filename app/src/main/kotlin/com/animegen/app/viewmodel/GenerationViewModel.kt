package com.animegen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegen.app.data.repository.AuthRepository
import com.animegen.app.data.repository.GenerationRepository
import com.animegen.app.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GenerationUiState(
    val prompt: String = "",
    val isLoading: Boolean = false,
    val generatedImageUrl: String? = null,
    val error: String? = null,
    val hasApiKey: Boolean = false
)

@HiltViewModel
class GenerationViewModel @Inject constructor(
    private val generationRepository: GenerationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerationUiState())
    val uiState: StateFlow<GenerationUiState> = _uiState.asStateFlow()

    init {
        checkApiKeyPresence()
    }

    private fun checkApiKeyPresence() {
        _uiState.value = _uiState.value.copy(
            hasApiKey = authRepository.hasDalleApiKey()
        )
    }

    fun updatePrompt(prompt: String) {
        _uiState.value = _uiState.value.copy(prompt = prompt)
    }

    fun generateImage() {
        if (_uiState.value.prompt.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter a prompt"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = generationRepository.generateAnimeImage(_uiState.value.prompt)

            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generatedImageUrl = result.data,
                        prompt = "",
                        error = null
                    )
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        generatedImageUrl = null
                    )
                }

                is Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearGeneratedImage() {
        _uiState.value = _uiState.value.copy(generatedImageUrl = null)
    }
}
