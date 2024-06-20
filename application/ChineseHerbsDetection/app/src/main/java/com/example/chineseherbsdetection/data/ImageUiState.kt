package com.example.chineseherbsdetection.data

import android.net.Uri

/**
 * Data class that represents the current UI state in terms of [imageUri]
 */
data class ImageUiState (
    /** Selected image uri */
    val imageUri: Uri = Uri.EMPTY
)