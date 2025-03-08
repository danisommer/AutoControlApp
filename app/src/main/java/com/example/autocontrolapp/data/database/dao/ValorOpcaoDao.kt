package com.example.autocontrolapp.data.database.dao

import androidx.room.*
import com.example.autocontrolapp.data.database.entity.ValorOpcao
import kotlinx.coroutines.flow.Flow

@Dao
interface ValorOpcaoDao {
    @Query("SELECT * FROM valores_opcao WHERE subtopicoId = :subtopicoId")
    fun getAllBySubtopicoId(subtopicoId: Long): Flow<List<ValorOpcao>>

    @Insert
    suspend fun insert(valorOpcao: ValorOpcao): Long

    @Insert
    suspend fun insertAll(valoresOpcao: List<ValorOpcao>): List<Long>

    @Update
    suspend fun update(valorOpcao: ValorOpcao)

    @Delete
    suspend fun delete(valorOpcao: ValorOpcao)

    @Query("DELETE FROM valores_opcao WHERE subtopicoId = :subtopicoId")
    suspend fun deleteAllFromSubtopico(subtopicoId: Long)
}
