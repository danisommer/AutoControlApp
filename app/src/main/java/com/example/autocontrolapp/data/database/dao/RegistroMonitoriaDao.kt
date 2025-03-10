package com.example.autocontrolapp.data.database.dao

import androidx.room.*
import com.example.autocontrolapp.data.database.entity.RegistroMonitoria

@Dao
interface RegistroMonitoriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(registro: RegistroMonitoria): Long

    @Update
    suspend fun update(registro: RegistroMonitoria)

    @Delete
    suspend fun delete(registro: RegistroMonitoria)

    @Query("SELECT * FROM registro_monitoria WHERE monitoriaId = :monitoriaId")
    suspend fun getRegistrosByMonitoria(monitoriaId: Long): List<RegistroMonitoria>

    @Query("SELECT * FROM registro_monitoria WHERE subtopicoId = :subtopicoId")
    suspend fun getRegistrosBySubtopico(subtopicoId: Long): List<RegistroMonitoria>
}
