package com.example.autocontrolapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notificacoes")
data class Notificacao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoriaId: Long,
    val dataNotificacao: Date,
    val mensagem: String,
    val visualizada: Boolean = false
)
