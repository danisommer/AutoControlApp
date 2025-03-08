package com.example.autocontrolapp.domain.usecase.monitoria

import com.example.autocontrolapp.domain.model.MonitoriaModel
import com.example.autocontrolapp.domain.repository.MonitoriaRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class ListarMonitoriasUseCase @Inject constructor(
    private val monitoriaRepository: MonitoriaRepository
) {
    fun obterMonitoriasPorCategoria(categoriaId: Int): Flow<List<MonitoriaModel>> {
        return monitoriaRepository.obterMonitoriasPorCategoria(categoriaId)
    }
    
    fun obterMonitoriasRecentes(dias: Int = 30): Flow<List<MonitoriaModel>> {
        val dataLimite = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -dias)
        }.time
        
        return monitoriaRepository.obterMonitoriasPorPeriodo(dataLimite, Date())
    }
    
    suspend fun obterMonitoriaPorId(id: Int): MonitoriaModel? {
        return monitoriaRepository.obterMonitoriaPorId(id)
    }
}