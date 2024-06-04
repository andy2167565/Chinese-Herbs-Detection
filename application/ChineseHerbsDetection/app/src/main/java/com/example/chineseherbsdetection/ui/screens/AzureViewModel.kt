package com.example.chineseherbsdetection.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chineseherbsdetection.ChineseHerbsDetectionApplication
import com.example.chineseherbsdetection.data.AzureResultsRepository
import com.example.chineseherbsdetection.model.Score
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Result screen
 */
sealed interface AzureUiState {
    data class Success(val results: Score) : AzureUiState
    object Error : AzureUiState
    object Loading : AzureUiState
}

class AzureViewModel(private val azureResultsRepository: AzureResultsRepository) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var azureUiState: AzureUiState by mutableStateOf(AzureUiState.Loading)
        private set

    /**
     * Gets Azure results information from the Azure API Retrofit service and updates the [Score].
     */
    fun getPredictScore(image: String) {
        viewModelScope.launch {
            azureUiState = try {
                val result = azureResultsRepository.getScore(image)
                AzureUiState.Success(result)
            } catch (e: IOException) {
                AzureUiState.Error
            } catch (e: HttpException) {
                AzureUiState.Error
            }
        }
    }

    /**
     * Factory for [AzureViewModel] that takes [AzureResultsRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChineseHerbsDetectionApplication)
                val azureResultsRepository = application.container.azureResultsRepository
                AzureViewModel(azureResultsRepository = azureResultsRepository)
            }
        }
    }
}