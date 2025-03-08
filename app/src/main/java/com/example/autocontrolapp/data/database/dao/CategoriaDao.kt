package com.example.autocontrolapp.data.database.dao

import androidx.room.*
import com.example.autocontrolapp.data.database.entity.Categoria
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY nome")
    fun getAll(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun getById(id: Long): Categoria?

    @Query("SELECT * FROM categorias WHERE proximaVerificacao <= :data ORDER BY proximaVerificacao")
    suspend fun getCategoriasParaVerificar(data: Date): List<Categoria>

    @Insert
    suspend fun insert(categoria: Categoria): Long

    @Update
    suspend fun update(categoria: Categoria)

    @Delete
    suspend fun delete(categoria: Categoria)

    @Query("UPDATE categorias SET ultimaVerificacao = :dataVerificacao, proximaVerificacao = :proximaVerificacao WHERE id = :categoriaId")
    suspend fun atualizarDatasVerificacao(categoriaId: Long, dataVerificacao: Date, proximaVerificacao: Date)
}