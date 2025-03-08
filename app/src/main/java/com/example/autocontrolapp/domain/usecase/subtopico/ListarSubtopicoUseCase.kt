package com.example.autocontrolapp.domain.usecase.subtopico

import com.example.autocontrolapp.domain.model.SubtopicoModel
import com.example.autocontrolapp.domain.repository.SubtopicoRepository
import com.example.autocontrolapp.domain.repository.ValorOpcaoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListarSubtopicoUseCase @Inject constructor(
    private val subtopicoRepository: SubtopicoRepository,
    private val valorOpcaoRepository: ValorOpcaoRepository
) {
    fun obterPorCategoria(categoriaId: Int): Flow<List<SubtopicoModel>> {
        return subtopicoRepository.obterSubtopicosPorCategoria(categoriaId)
            .map { subtopicos ->
                subtopicos.map { subtopico ->
                    val valoresOpcoes = valorOpcaoRepository.obterValoresOpcaoPorSubtopico(subtopico.id)
                    subtopico.copy(valoresOpcoes = valoresOpcoes)
                }
            }
    }
    
    suspend fun obterPorId(id: Int): SubtopicoModel? {
        val subtopico = subtopicoRepository.obterSubtopicoPorId(id) ?: return null
        val valoresOpcoes = valorOpcaoRepository.obterValoresOpcaoPorSubtopico(id)
        return subtopico.copy(valoresOpcoes = valoresOpcoes)
    }
}