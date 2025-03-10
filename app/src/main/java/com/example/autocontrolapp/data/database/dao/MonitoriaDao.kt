package com.example.autocontrolapp.data.database.dao

import androidx.room.*
import com.example.autocontrolapp.data.database.entity.Monitoria

@Dao
interface MonitoriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(monitoria: Monitoria): Long

    @Update
    suspend fun update(monitoria: Monitoria)

    @Delete
    suspend fun delete(monitoria: Monitoria)

    @Query("SELECT * FROM monitorias WHERE categoriaId = :categoriaId")
    suspend fun getMonitoriasByCategoria(categoriaId: Long): List<Monitoria>

    @Query("SELECT * FROM monitorias ORDER BY data DESC")
    suspend fun getAllMonitorias(): List<Monitoria>
}
