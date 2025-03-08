package com.example.autocontrolapp.domain.model

import java.util.Date

data class MonitoriaModel(
    val id: Int = 0,
    val categoriaId: Int,
    val dataRealizacao: Date,
    val realizadaPor: String,
    val observacoes: String = "",
    val respostas: List<RespostaMonitoriaModel> = emptyList()
)

data class RespostaMonitoriaModel(
    val id: Int = 0,
    val monitoriaId: Int,
    val subtopicoId: Int,
    val valorResposta: String, // Armazena a resposta independente do tipo
    val observacoes: String = ""
)