package com.example.autocontrolapp.domain.usecase.categoria

import com.example.autocontrolapp.domain.model.CategoriaModel
import com.example.autocontrolapp.domain.repository.CategoriaRepository
import javax.inject.Inject

class AdicionarCategoriaUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) {
    suspend operator fun invoke(categoria: CategoriaModel): Result<CategoriaModel> {
        return try {
            if (categoria.nome.isBlank()) {
                return Result.failure(IllegalArgumentException("O nome da categoria não pode estar vazio"))
            }
            
            if (categoria.periodo <= 0) {
                return Result.failure(IllegalArgumentException("O período deve ser maior que zero"))
            }
            
            val idInserida = categoriaRepository.inserirCategoria(categoria)
            Result.success(categoria.copy(id = idInserida.toInt()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}