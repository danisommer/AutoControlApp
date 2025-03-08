package com.example.autocontrolapp.data.database.dao

import androidx.room.*
import com.example.autocontrolapp.data.database.entity.RespostaMonitoria

@Dao
interface RespostaMonitoriaDao {
    @Query("SELECT * FROM respostas_monitoria WHERE monitoriaId = :monitoriaId")
    suspend fun getAllByMonitoriaId(monitoriaId: Long): List<RespostaMonitoria>

    @Insert
    suspend fun insert(respostaMonitoria: RespostaMonitoria): Long

    @Insert
    suspend fun insertAll(respostasMonitoria: List<RespostaMonitoria>): List<Long>

    @Update
    suspend fun update(respostaMonitoria: RespostaMonitoria)

    @Query("DELETE FROM respostas_monitoria WHERE monitoriaId = :monitoriaId")
    suspend fun deleteAllFromMonitoria(monitoriaId: Long)

    @Transaction
    @Query("SELECT * FROM respostas_monitoria WHERE monitoriaId = :monitoriaId AND subtopicoId = :subtopicoId")
    suspend fun getRespostaByMonitoriaESubtopico(monitoriaId: Long, subtopicoId: Long): RespostaMonitoria?
}