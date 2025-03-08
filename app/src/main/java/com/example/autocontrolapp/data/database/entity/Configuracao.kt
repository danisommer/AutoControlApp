package com.example.autocontrolapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuracoes")
data class Configuracao(
    @PrimaryKey val id: Int = 1, // Apenas um registro
    val notificacoesAtivadas: Boolean,
    val horarioNotificacao: String // Ex: "08:00"
)
