package com.example.autocontrolapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autocontrolapp.data.repository.CategoriaRepository

class CategoriaViewModelFactory(
    private val repository: CategoriaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
