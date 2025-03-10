package com.example.autocontrolapp.data.repository

import com.example.autocontrolapp.data.database.dao.SubtopicoDao
import com.example.autocontrolapp.data.database.entity.Subtopico
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubtopicoRepository(private val subtopicoDao: SubtopicoDao) {

    suspend fun insertSubtopico(subtopico: Subtopico): Long = withContext(Dispatchers.IO) {
        subtopicoDao.insert(subtopico)
    }

    suspend fun updateSubtopico(subtopico: Subtopico) = withContext(Dispatchers.IO) {
        subtopicoDao.update(subtopico)
    }

    suspend fun deleteSubtopico(subtopico: Subtopico) = withContext(Dispatchers.IO) {
        subtopicoDao.delete(subtopico)
    }

    suspend fun getSubtopicosByCategoria(categoriaId: Long): List<Subtopico> = withContext(Dispatchers.IO) {
        subtopicoDao.getSubtopicosByCategoria(categoriaId)
    }
}
