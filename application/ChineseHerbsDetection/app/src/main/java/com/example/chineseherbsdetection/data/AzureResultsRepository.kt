package com.example.chineseherbsdetection.data

import com.example.chineseherbsdetection.model.Score
import com.example.chineseherbsdetection.network.AzureApiService

/**
 * Repository that fetch results from Azure Api.
 */
interface AzureResultsRepository {
    /** Fetches results from Azure Api */
    suspend fun getScore(image: String): Score
}

/**
 * Network Implementation of Repository that fetch results from Azure Api.
 */
class NetworkAzureResultsRepository(
    private val azureApiService: AzureApiService
) : AzureResultsRepository {
    /** Fetches results from Azure Api */
    override suspend fun getScore(image: String): Score = azureApiService.getScore(image)
}