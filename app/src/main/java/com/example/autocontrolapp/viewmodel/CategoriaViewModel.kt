package com.example.autocontrolapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocontrolapp.data.database.entity.Categoria
import com.example.autocontrolapp.data.repository.CategoriaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    fun addCategoria(categoria: Categoria) {
        viewModelScope.launch {
            categoriaRepository.insertCategoria(categoria)
        }
    }

    fun updateCategoria(categoria: Categoria) {
        viewModelScope.launch {
            categoriaRepository.updateCategoria(categoria)
        }
    }

    fun deleteCategoria(categoria: Categoria) {
        viewModelScope.launch {
            categoriaRepository.deleteCategoria(categoria)
        }
    }

    suspend fun getAllCategorias() = categoriaRepository.getAllCategorias()
}
