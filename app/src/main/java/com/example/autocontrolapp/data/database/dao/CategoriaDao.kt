package com.example.autocontrolapp.data.database.dao

import androidx.room.*
import com.example.autocontrolapp.data.database.entity.Categoria

@Dao
interface CategoriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria): Long

    @Update
    suspend fun update(categoria: Categoria)

    @Delete
    suspend fun delete(categoria: Categoria)

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun getCategoriaById(id: Long): Categoria?

    @Query("SELECT * FROM categorias")
    suspend fun getAllCategorias(): List<Categoria>
}
