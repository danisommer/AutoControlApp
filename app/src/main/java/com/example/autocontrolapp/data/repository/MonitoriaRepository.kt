package com.example.autocontrolapp.data.repository

import com.example.autocontrolapp.data.database.dao.MonitoriaDao
import com.example.autocontrolapp.data.database.entity.Monitoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MonitoriaRepository(private val monitoriaDao: MonitoriaDao) {

    suspend fun insertMonitoria(monitoria: Monitoria): Long = withContext(Dispatchers.IO) {
        monitoriaDao.insert(monitoria)
    }

    suspend fun updateMonitoria(monitoria: Monitoria) = withContext(Dispatchers.IO) {
        monitoriaDao.update(monitoria)
    }

    suspend fun deleteMonitoria(monitoria: Monitoria) = withContext(Dispatchers.IO) {
        monitoriaDao.delete(monitoria)
    }

    suspend fun getMonitoriasByCategoria(categoriaId: Long): List<Monitoria> = withContext(Dispatchers.IO) {
        monitoriaDao.getMonitoriasByCategoria(categoriaId)
    }

    suspend fun getAllMonitorias(): List<Monitoria> = withContext(Dispatchers.IO) {
        monitoriaDao.getAllMonitorias()
    }
}
