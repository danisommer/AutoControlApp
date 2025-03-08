package com.example.autocontrolapp.domain.model

data class SubtopicoModel(
    val id: Int = 0,
    val categoriaId: Int,
    val nome: String,
    val descricao: String = "",
    val tipoDado: TipoDadoEnum,
    val obrigatorio: Boolean = true,
    val ordem: Int = 0,
    val valoresOpcoes: List<ValorOpcaoModel> = emptyList()
)

enum class TipoDadoEnum {
    OK_NAO_OK,  // Para verificações simples como "OK" ou "Não OK"
    TEXTO,      // Para campos de texto livre (ex: nome do supervisor)
    NUMERO,     // Para valores numéricos
    SELECAO     // Para selecionar de uma lista predefinida de opções
}