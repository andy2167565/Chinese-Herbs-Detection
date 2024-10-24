package com.example.chineseherbsdetection.network

import com.example.chineseherbsdetection.model.Score
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AzureApiService {
    @Headers(
        "Authorization: Bearer xLPVNhQ27CRIUT5HOK35H6YCdL710gwp",
        "Content-Type: application/json"
    )
    @POST("score")
    suspend fun getScore(@Body image: String): Score
}