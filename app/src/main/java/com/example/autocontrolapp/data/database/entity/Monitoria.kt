package com.example.autocontrolapp.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "monitorias")
data class Monitoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoriaId: Long,
    val dataHora: Date, // Data e hora em que a vistoria foi feita
    val usuarioId: Long? // Se quiser associar um usuário responsável
)
