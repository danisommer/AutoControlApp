package com.example.autocontrolapp.domain.usecase.subtopico

import com.example.autocontrolapp.domain.model.SubtopicoModel
import com.example.autocontrolapp.domain.model.ValorOpcaoModel
import com.example.autocontrolapp.domain.repository.CategoriaRepository
import com.example.autocontrolapp.domain.repository.SubtopicoRepository
import com.example.autocontrolapp.domain.repository.ValorOpcaoRepository
import javax.inject.Inject

class AdicionarSubtopicoUseCase @Inject constructor(
    private val subtopicoRepository: SubtopicoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val valorOpcaoRepository: ValorOpcaoRepository
) {
    suspend operator fun invoke(subtopico: SubtopicoModel): Result<SubtopicoModel> {
        return try {
            if (subtopico.nome.isBlank()) {
                return Result.failure(IllegalArgumentException("O nome do subtópico não pode estar vazio"))
            }
            
            // Verifica se a categoria existe
            val categoriaExiste = categoriaRepository.verificaSeExiste(subtopico.categoriaId)
            if (!categoriaExiste) {
                return Result.failure(IllegalArgumentException("Categoria não encontrada"))
            }
            
            // Salva o subtópico
            val subtopicoId = subtopicoRepository.inserirSubtopico(subtopico)
            
            // Se tiver valores de opção, salva-os também
            if (subtopico.valoresOpcoes.isNotEmpty()) {
                subtopico.valoresOpcoes.forEach { opcao ->
                    valorOpcaoRepository.inserirValorOpcao(
                        opcao.copy(subtopicoId = subtopicoId.toInt())
                    )
                }
            }
            
            Result.success(subtopico.copy(id = subtopicoId.toInt()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}