package com.example.autocontrolapp.domain.usecase.categoria

import com.example.autocontrolapp.domain.model.CategoriaModel
import com.example.autocontrolapp.domain.repository.CategoriaRepository
import java.util.Date
import javax.inject.Inject

class AtualizarCategoriaUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) {
    suspend operator fun invoke(categoria: CategoriaModel): Result<Unit> {
        return try {
            if (categoria.id <= 0) {
                return Result.failure(IllegalArgumentException("ID da categoria inválido"))
            }
            
            if (categoria.nome.isBlank()) {
                return Result.failure(IllegalArgumentException("O nome da categoria não pode estar vazio"))
            }
            
            categoriaRepository.atualizarCategoria(categoria)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Função auxiliar para marcar uma categoria como verificada
    suspend fun marcarVerificacao(categoriaId: Int, dataVerificacao: Date = Date()): Result<Unit> {
        return try {
            val categoria = categoriaRepository.obterCategoriaPorId(categoriaId)
                ?: return Result.failure(IllegalArgumentException("Categoria não encontrada"))
            
            // Calcula a próxima verificação com base na periodicidade
            val cal = java.util.Calendar.getInstance()
            cal.time = dataVerificacao
            cal.add(java.util.Calendar.DAY_OF_MONTH, categoria.periodo)
            val proximaVerificacao = cal.time
            
            val categoriaAtualizada = categoria.copy(
                ultimaVerificacao = dataVerificacao,
                proximaVerificacao = proximaVerificacao
            )
            
            categoriaRepository.atualizarCategoria(categoriaAtualizada)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}