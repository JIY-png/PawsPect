package com.example.pawspect.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawspect.data.model.BreedPrediction
import com.example.pawspect.data.repository.DogBreedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ResultsUiState {
    data object Loading : ResultsUiState
    data class Success(val predictions: List<BreedPrediction>) : ResultsUiState
    data class Error(val message: String) : ResultsUiState
}

class ResultsViewModel(
    private val repository: DogBreedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    fun identifyBreed(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = ResultsUiState.Loading
            try {
                val results = repository.classifyDogBreed(context, imageUri)
                _uiState.value = ResultsUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = ResultsUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}
