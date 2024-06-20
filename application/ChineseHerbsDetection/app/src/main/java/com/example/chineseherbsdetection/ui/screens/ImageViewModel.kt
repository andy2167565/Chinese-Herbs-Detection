package com.example.chineseherbsdetection.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.chineseherbsdetection.data.ImageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ImageViewModel : ViewModel() {
    /**
     * Current image state
     */
    private val _uiState = MutableStateFlow(ImageUiState())
    val uiState: StateFlow<ImageUiState> = _uiState.asStateFlow()

    /**
     * Set the [uri] of image for current state.
     */
    fun setUri(selectedUri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(imageUri = selectedUri)
        }
    }

    /**
     * Reset the state
     */
    fun resetOrder() {
        _uiState.value = ImageUiState()
    }
}