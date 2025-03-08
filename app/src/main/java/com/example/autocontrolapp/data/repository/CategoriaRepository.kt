package com.example.autocontrolapp.data.repository

import com.example.autocontrolapp.data.database.dao.CategoriaDao
import com.example.autocontrolapp.data.database.dao.SubtopicoDao
import com.example.autocontrolapp.data.database.entity.Categoria
import java.util.*

class CategoriaRepository(
    private val categoriaDao: CategoriaDao,
    private val subtopicoDao: SubtopicoDao
) {
    fun obterTodasCategorias() = categoriaDao.getAll()

    suspend fun obterCategoriaPorId(id: Long) = categoriaDao.getById(id)

    suspend fun obterCategoriasParaVerificar(data: Date = Date()) =
        categoriaDao.getCategoriasParaVerificar(data)

    suspend fun adicionarCategoria(categoria: Categoria): Long {
        return categoriaDao.insert(categoria)
    }

    suspend fun atualizarCategoria(categoria: Categoria) {
        categoriaDao.update(categoria)
    }

    suspend fun excluirCategoria(categoria: Categoria) {
        categoriaDao.delete(categoria)
    }

    suspend fun atualizarDatasVerificacao(
        categoriaId: Long,
        dataVerificacao: Date = Date(),
        periodicidade: Int
    ) {
        val calendario = Calendar.getInstance()
        calendario.time = dataVerificacao
        calendario.add(Calendar.DAY_OF_MONTH, periodicidade)
        val proximaVerificacao = calendario.time

        categoriaDao.atualizarDatasVerificacao(
            categoriaId = categoriaId,
            dataVerificacao = dataVerificacao,
            proximaVerificacao = proximaVerificacao
        )
    }
}