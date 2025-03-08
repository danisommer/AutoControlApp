package com.example.autocontrolapp.domain.model

data class ValorOpcaoModel(
    val id: Int = 0,
    val subtopicoId: Int,
    val valor: String,
    val ordem: Int = 0
)