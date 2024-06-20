package com.example.chineseherbsdetection.network

import com.example.chineseherbsdetection.model.Score
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AzureApiService {
    @Headers(
        "Authorization: Bearer 2hPdlHMgioxYpVRHPBqVFekoXvIYWI2K",
        "Content-Type: application/json",
        "azureml-model-deployment: herbs-detect-server"
    )
    @POST("score")
    suspend fun getScore(@Body image: String): Score
}