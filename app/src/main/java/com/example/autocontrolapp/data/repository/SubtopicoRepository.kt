package com.example.autocontrolapp.data.repository

import com.example.autocontrolapp.data.database.dao.SubtopicoDao
import com.example.autocontrolapp.data.database.entity.Subtopico

class SubtopicoRepository(
    private val subtopicoDao: SubtopicoDao
) {
    fun obterTodosSubtopicos() = subtopicoDao.getAll()

    suspend fun obterSubtopicoPorId(id: Long) = subtopicoDao.getById(id)
    
    suspend fun obterSubtopicosPorCategoria(categoriaId: Long) = 
        subtopicoDao.getByCategoria(categoriaId)

    suspend fun adicionarSubtopico(subtopico: Subtopico): Long {
        return subtopicoDao.insert(subtopico)
    }

    suspend fun atualizarSubtopico(subtopico: Subtopico) {
        subtopicoDao.update(subtopico)
    }

    suspend fun excluirSubtopico(subtopico: Subtopico) {
        subtopicoDao.delete(subtopico)
    }
    
    suspend fun marcarSubtopicoComoConcluido(id: Long, concluido: Boolean) {
        subtopicoDao.atualizarStatus(id, concluido)
    }
}