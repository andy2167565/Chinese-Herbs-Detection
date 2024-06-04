package com.example.chineseherbsdetection.data

import com.example.chineseherbsdetection.network.AzureApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val azureResultsRepository: AzureResultsRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://chinese-herbs-ml-xohnw.eastus2.inference.ml.azure.com"

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
//        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: AzureApiService by lazy {
        retrofit.create(AzureApiService::class.java)
    }

    /**
     * DI implementation for Azure results repository
     */
    override val azureResultsRepository: AzureResultsRepository by lazy {
        NetworkAzureResultsRepository(retrofitService)
    }
}