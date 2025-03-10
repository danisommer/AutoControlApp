package com.example.autocontrolapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocontrolapp.data.database.entity.Monitoria
import com.example.autocontrolapp.data.repository.MonitoriaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitoriaViewModel @Inject constructor(
    private val monitoriaRepository: MonitoriaRepository
) : ViewModel() {

    fun addMonitoria(monitoria: Monitoria) {
        viewModelScope.launch {
            monitoriaRepository.insertMonitoria(monitoria)
        }
    }

    fun updateMonitoria(monitoria: Monitoria) {
        viewModelScope.launch {
            monitoriaRepository.updateMonitoria(monitoria)
        }
    }

    fun deleteMonitoria(monitoria: Monitoria) {
        viewModelScope.launch {
            monitoriaRepository.deleteMonitoria(monitoria)
        }
    }

    suspend fun getAllMonitorias() = monitoriaRepository.getAllMonitorias()
}
