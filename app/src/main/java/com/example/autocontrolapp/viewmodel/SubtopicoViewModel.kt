package com.example.autocontrolapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocontrolapp.data.database.entity.Subtopico
import com.example.autocontrolapp.data.repository.SubtopicoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubtopicoViewModel @Inject constructor(
    private val subtopicoRepository: SubtopicoRepository
) : ViewModel() {

    fun addSubtopico(subtopico: Subtopico) {
        viewModelScope.launch {
            subtopicoRepository.insertSubtopico(subtopico)
        }
    }

    fun updateSubtopico(subtopico: Subtopico) {
        viewModelScope.launch {
            subtopicoRepository.updateSubtopico(subtopico)
        }
    }

    fun deleteSubtopico(subtopico: Subtopico) {
        viewModelScope.launch {
            subtopicoRepository.deleteSubtopico(subtopico)
        }
    }

    suspend fun getSubtopicosByCategoria(categoriaId: Long) =
        subtopicoRepository.getSubtopicosByCategoria(categoriaId)
}
