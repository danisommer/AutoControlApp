package com.example.autocontrolapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.autocontrolapp.data.database.entity.Notificacao
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface NotificacaoDao {
    @Query("SELECT * FROM notificacoes ORDER BY dataNotificacao DESC")
    fun getAll(): Flow<List<Notificacao>>

    @Query("SELECT * FROM notificacoes WHERE visualizada = 0 ORDER BY dataNotificacao")
    suspend fun getNotificacoesNaoVisualizadas(): List<Notificacao>

    @Insert
    suspend fun insert(notificacao: Notificacao): Long

    @Update
    suspend fun update(notificacao: Notificacao)

    @Query("UPDATE notificacoes SET visualizada = 1 WHERE id = :notificacaoId")
    suspend fun marcarComoVisualizada(notificacaoId: Long)

    @Query("DELETE FROM notificacoes WHERE dataNotificacao < :data")
    suspend fun limparNotificacoesAntigas(data: Date)
}