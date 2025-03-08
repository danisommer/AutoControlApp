package com.example.autocontrolapp.domain.model

import java.util.Date

data class NotificacaoModel(
    val id: Int = 0,
    val categoriaId: Int,
    val titulo: String,
    val mensagem: String,
    val dataNotificacao: Date,
    val visualizada: Boolean = false,
    val concluida: Boolean = false
)