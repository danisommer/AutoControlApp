package com.example.autocontrolapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocontrolapp.data.database.entity.RegistroMonitoria
import com.example.autocontrolapp.data.repository.RegistroMonitoriaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroMonitoriaViewModel @Inject constructor(
    private val registroMonitoriaRepository: RegistroMonitoriaRepository
) : ViewModel() {

    fun addRegistro(registro: RegistroMonitoria) {
        viewModelScope.launch {
            registroMonitoriaRepository.insertRegistro(registro)
        }
    }

    fun updateRegistro(registro: RegistroMonitoria) {
        viewModelScope.launch {
            registroMonitoriaRepository.updateRegistro(registro)
        }
    }

    fun deleteRegistro(registro: RegistroMonitoria) {
        viewModelScope.launch {
            registroMonitoriaRepository.deleteRegistro(registro)
        }
    }

    suspend fun getRegistrosByMonitoria(monitoriaId: Long) =
        registroMonitoriaRepository.getRegistrosByMonitoria(monitoriaId)
}
