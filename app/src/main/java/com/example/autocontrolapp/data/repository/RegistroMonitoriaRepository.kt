package com.example.autocontrolapp.data.repository

import com.example.autocontrolapp.data.database.dao.RegistroMonitoriaDao
import com.example.autocontrolapp.data.database.entity.RegistroMonitoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistroMonitoriaRepository(private val registroMonitoriaDao: RegistroMonitoriaDao) {

    suspend fun insertRegistro(registro: RegistroMonitoria): Long = withContext(Dispatchers.IO) {
        registroMonitoriaDao.insert(registro)
    }

    suspend fun updateRegistro(registro: RegistroMonitoria) = withContext(Dispatchers.IO) {
        registroMonitoriaDao.update(registro)
    }

    suspend fun deleteRegistro(registro: RegistroMonitoria) = withContext(Dispatchers.IO) {
        registroMonitoriaDao.delete(registro)
    }

    suspend fun getRegistrosByMonitoria(monitoriaId: Long): List<RegistroMonitoria> = withContext(Dispatchers.IO) {
        registroMonitoriaDao.getRegistrosByMonitoria(monitoriaId)
    }

    suspend fun getRegistrosBySubtopico(subtopicoId: Long): List<RegistroMonitoria> = withContext(Dispatchers.IO) {
        registroMonitoriaDao.getRegistrosBySubtopico(subtopicoId)
    }
}
