package com.animegen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegen.app.data.db.ImageEntity
import com.animegen.app.data.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GalleryUiState(
    val images: List<ImageEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedImage: ImageEntity? = null,
    val searchQuery: String = "",
    val favoriteOnly: Boolean = false
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        loadImages()
    }

    private fun loadImages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val imageFlow = if (_uiState.value.favoriteOnly) {
                    galleryRepository.getFavoriteImages()
                } else {
                    galleryRepository.getAllImages()
                }

                imageFlow.collectLatest { images ->
                    _uiState.value = _uiState.value.copy(
                        images = images,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load gallery"
                )
            }
        }
    }

    fun deleteImage(imageId: Long) {
        viewModelScope.launch {
            try {
                galleryRepository.deleteImage(imageId)
                if (_uiState.value.selectedImage?.id == imageId) {
                    _uiState.value = _uiState.value.copy(selectedImage = null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete image: ${e.message}"
                )
            }
        }
    }

    fun toggleFavorite(imageId: Long) {
        viewModelScope.launch {
            try {
                galleryRepository.toggleFavorite(imageId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to toggle favorite: ${e.message}"
                )
            }
        }
    }

    fun selectImage(image: ImageEntity) {
        _uiState.value = _uiState.value.copy(selectedImage = image)
    }

    fun deselectImage() {
        _uiState.value = _uiState.value.copy(selectedImage = null)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        // TODO: Implement search filtering
    }

    fun toggleFavoriteFilter() {
        _uiState.value = _uiState.value.copy(
            favoriteOnly = !_uiState.value.favoriteOnly
        )
        loadImages()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
