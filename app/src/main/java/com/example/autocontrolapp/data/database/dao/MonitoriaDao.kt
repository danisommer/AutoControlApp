package com.example.autocontrolapp.data.database.dao

import androidx.room.*
import com.example.autocontrolapp.data.database.entity.Monitoria
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitoriaDao {
    @Transaction
    @Query("SELECT * FROM monitorias WHERE categoriaId = :categoriaId ORDER BY dataRealizacao DESC")
    fun getAllByCategoriaId(categoriaId: Long): Flow<List<Monitoria>>

    @Query("SELECT * FROM monitorias WHERE id = :id")
    suspend fun getById(id: Long): Monitoria?

    @Insert
    suspend fun insert(monitoria: Monitoria): Long

    @Update
    suspend fun update(monitoria: Monitoria)

    @Delete
    suspend fun delete(monitoria: Monitoria)

    @Query("UPDATE monitorias SET relatorioGerado = 1, caminhoRelatorio = :caminhoRelatorio WHERE id = :monitoriaId")
    suspend fun atualizarRelatorio(monitoriaId: Long, caminhoRelatorio: String)

    @Query("SELECT * FROM monitorias WHERE relatorioGerado = 0")
    suspend fun getMonitoriasParaGerarRelatorio(): List<Monitoria>
}