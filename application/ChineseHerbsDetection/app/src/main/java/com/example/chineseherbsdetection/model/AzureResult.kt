package com.example.chineseherbsdetection.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This data class defines a JSON object of the returned API result.
 */
@Serializable
data class Score(
    @SerialName(value = "Confidence")
    val confidence: String,
    @SerialName(value = "Chinese name")
    val chiName: String,
    @SerialName(value = "Scientific Name")
    val sciName: String,
    @SerialName(value = "Category")
    val category: String,
    val efficacy: String
)