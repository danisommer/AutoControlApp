package com.example.autocontrolapp.domain.model

import java.util.Date

data class CategoriaModel(
    val id: Int = 0,
    val nome: String,
    val descricao: String = "",
    val periodo: Int, // em dias
    val ultimaVerificacao: Date? = null,
    val proximaVerificacao: Date? = null,
    val ativa: Boolean = true
)