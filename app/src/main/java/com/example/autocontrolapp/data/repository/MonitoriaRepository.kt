package com.example.autocontrolapp.data.repository

import com.example.autocontrolapp.data.database.dao.MonitoriaDao
import com.example.autocontrolapp.data.database.entity.Monitoria
import java.util.*

class MonitoriaRepository(
    private val monitoriaDao: MonitoriaDao
) {
    fun obterTodasMonitorias() = monitoriaDao.getAll()

    suspend fun obterMonitoriaPorId(id: Long) = monitoriaDao.getById(id)
    
    suspend fun obterMonitoriasPorCategoria(categoriaId: Long) = 
        monitoriaDao.getByCategoria(categoriaId)
    
    suspend fun obterMonitoriasPorData(data: Date) =
        monitoriaDao.getByData(data)
    
    suspend fun obterMonitoriasRecentes(limite: Int = 10) = 
        monitoriaDao.getRecentes(limite)

    suspend fun adicionarMonitoria(monitoria: Monitoria): Long {
        return monitoriaDao.insert(monitoria)
    }

    suspend fun atualizarMonitoria(monitoria: Monitoria) {
        monitoriaDao.update(monitoria)
    }

    suspend fun excluirMonitoria(monitoria: Monitoria) {
        monitoriaDao.delete(monitoria)
    }
    
    suspend fun obterEstatisticasDeMonitorias(categoriaId: Long, periodoInicio: Date, periodoFim: Date) =
        monitoriaDao.getEstatisticas(categoriaId, periodoInicio, periodoFim)
}