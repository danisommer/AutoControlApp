package com.example.autocontrolapp.data.database.dao

import androidx.room.*
import com.example.autocontrolapp.data.database.entity.Subtopico
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtopicoDao {
    @Query("SELECT * FROM subtopicos WHERE categoriaId = :categoriaId ORDER BY nome")
    fun getAllByCategoriaId(categoriaId: Long): Flow<List<Subtopico>>

    @Query("SELECT * FROM subtopicos WHERE id = :id")
    suspend fun getById(id: Long): Subtopico?

    @Insert
    suspend fun insert(subtopico: Subtopico): Long

    @Insert
    suspend fun insertAll(subtopicos: List<Subtopico>): List<Long>

    @Update
    suspend fun update(subtopico: Subtopico)

    @Delete
    suspend fun delete(subtopico: Subtopico)

    @Query("DELETE FROM subtopicos WHERE categoriaId = :categoriaId")
    suspend fun deleteAllFromCategoria(categoriaId: Long)
}