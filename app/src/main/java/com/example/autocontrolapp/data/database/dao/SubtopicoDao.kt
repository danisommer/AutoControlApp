package com.example.autocontrolapp.data.database.dao

import androidx.room.*
import com.example.autocontrolapp.data.database.entity.Subtopico

@Dao
interface SubtopicoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subtopico: Subtopico): Long

    @Update
    suspend fun update(subtopico: Subtopico)

    @Delete
    suspend fun delete(subtopico: Subtopico)

    @Query("SELECT * FROM subtopicos WHERE categoriaId = :categoriaId")
    suspend fun getSubtopicosByCategoria(categoriaId: Long): List<Subtopico>
}
