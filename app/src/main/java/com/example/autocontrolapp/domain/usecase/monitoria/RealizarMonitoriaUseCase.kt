package com.example.autocontrolapp.domain.usecase.monitoria

import com.example.autocontrolapp.domain.model.MonitoriaModel
import com.example.autocontrolapp.domain.repository.MonitoriaRepository
import com.example.autocontrolapp.domain.repository.SubtopicoRepository
import com.example.autocontrolapp.domain.usecase.categoria.AtualizarCategoriaUseCase
import java.util.Date
import javax.inject.Inject

class RealizarMonitoriaUseCase @Inject constructor(
    private val monitoriaRepository: MonitoriaRepository,
    private val subtopicoRepository: SubtopicoRepository,
    private val atualizarCategoriaUseCase: AtualizarCategoriaUseCase
) {
    suspend operator fun invoke(monitoria: MonitoriaModel): Result<MonitoriaModel> {
        return try {
            // Validações
            if (monitoria.categoriaId <= 0) {
                return Result.failure(IllegalArgumentException("ID de categoria inválido"))
            }
            
            if (monitoria.realizadaPor.isBlank()) {
                return Result.failure(IllegalArgumentException("É necessário informar quem realizou a monitoria"))
            }
            
            // Verifica se todos os subtópicos obrigatórios foram respondidos
            val subtopicosObrigatorios = subtopicoRepository.obterSubtopicosObrigatoriosPorCategoria(monitoria.categoriaId)
            val idsRespondidos = monitoria.respostas.map { it.subtopicoId }
            
            val naoRespondidos = subtopicosObrigatorios.filter { it.id !in idsRespondidos }
            if (naoRespondidos.isNotEmpty()) {
                val nomes = naoRespondidos.joinToString(", ") { it.nome }
                return Result.failure(IllegalArgumentException("Subtópicos obrigatórios não respondidos: $nomes"))
            }
            
            // Salva a monitoria e suas respostas
            val id = monitoriaRepository.inserirMonitoria(monitoria)
            
            // Atualiza a data de verificação da categoria
            atualizarCategoriaUseCase.marcarVerificacao(
                monitoria.categoriaId,
                monitoria.dataRealizacao
            )
            
            Result.success(monitoria.copy(id = id.toInt()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}