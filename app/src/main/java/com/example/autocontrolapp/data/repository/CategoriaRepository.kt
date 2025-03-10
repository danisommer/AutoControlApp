package com.example.autocontrolapp.data.repository

import com.example.autocontrolapp.data.database.dao.CategoriaDao
import com.example.autocontrolapp.data.database.entity.Categoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoriaRepository(private val categoriaDao: CategoriaDao) {

    suspend fun insertCategoria(categoria: Categoria): Long = withContext(Dispatchers.IO) {
        categoriaDao.insert(categoria)
    }

    suspend fun updateCategoria(categoria: Categoria) = withContext(Dispatchers.IO) {
        categoriaDao.update(categoria)
    }

    suspend fun deleteCategoria(categoria: Categoria) = withContext(Dispatchers.IO) {
        categoriaDao.delete(categoria)
    }

    suspend fun getCategoriaById(id: Long): Categoria? = withContext(Dispatchers.IO) {
        categoriaDao.getCategoriaById(id)
    }

    suspend fun getAllCategorias(): List<Categoria> = withContext(Dispatchers.IO) {
        categoriaDao.getAllCategorias()
    }
}
