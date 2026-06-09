package com.example.pawspect.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pawspect.data.repository.DogBreedRepository
import com.example.pawspect.ui.viewmodel.ResultsViewModel

class ViewModelFactory(private val repository: DogBreedRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResultsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
