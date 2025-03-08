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
    val periodicidade: Int, // em dias
    val ultimaVerificacao: Date?,
    val proximaVerificacao: Date?
)
