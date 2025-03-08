package com.example.autocontrolapp.domain.usecase.categoria

import com.example.autocontrolapp.domain.model.CategoriaModel
import com.example.autocontrolapp.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListarCategoriaUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) {
    operator fun invoke(apenasAtivas: Boolean = true): Flow<List<CategoriaModel>> {
        return if (apenasAtivas) {
            categoriaRepository.obterCategoriasAtivas()
        } else {
            categoriaRepository.obterTodasCategorias()
        }
    }
}