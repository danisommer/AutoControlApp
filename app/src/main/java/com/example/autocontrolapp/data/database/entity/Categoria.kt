package com.example.autocontrolapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val descricao: String,
    val periodicidade: Int,       // Em dias
    val ultimaVerificacao: Date?, // Data/hora da última verificação
    val proximaVerificacao: Date? // Data/hora da próxima verificação
)
