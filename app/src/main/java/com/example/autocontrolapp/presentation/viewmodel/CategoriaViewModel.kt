package com.example.autocontrolapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocontrolapp.domain.model.CategoriaModel
import com.example.autocontrolapp.domain.usecase.categoria.AdicionarCategoriaUseCase
import com.example.autocontrolapp.domain.usecase.categoria.AtualizarCategoriaUseCase
import com.example.autocontrolapp.domain.usecase.categoria.ListarCategoriaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaViewModel @Inject constructor(
    private val listarCategoriaUseCase: ListarCategoriaUseCase,
    private val adicionarCategoriaUseCase: AdicionarCategoriaUseCase,
    private val atualizarCategoriaUseCase: AtualizarCategoriaUseCase
) : ViewModel() {

    private val _categorias = MutableStateFlow<List<CategoriaModel>>(emptyList())
    val categorias: StateFlow<List<CategoriaModel>> = _categorias.asStateFlow()

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    private val _operacaoSucesso = MutableStateFlow<Boolean?>(null)
    val operacaoSucesso: StateFlow<Boolean?> = _operacaoSucesso.asStateFlow()

    init {
        carregarCategorias()
    }

    fun carregarCategorias(apenasAtivas: Boolean = true) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            
            listarCategoriaUseCase(apenasAtivas)
                .catch { e ->
                    _erro.value = e.message ?: "Erro ao carregar categorias"
                    _carregando.value = false
                }
                .collectLatest { lista ->
                    _categorias.value = lista
                    _carregando.value = false
                }
        }
    }

    fun adicionarCategoria(categoria: CategoriaModel) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            _operacaoSucesso.value = null
            
            val resultado = adicionarCategoriaUseCase(categoria)
            
            resultado.fold(
                onSuccess = {
                    _operacaoSucesso.value = true
                    carregarCategorias()
                },
                onFailure = { e ->
                    _erro.value = e.message ?: "Erro ao adicionar categoria"
                    _operacaoSucesso.value = false
                    _carregando.value = false
                }
            )
        }
    }

    fun atualizarCategoria(categoria: CategoriaModel) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            _operacaoSucesso.value = null
            
            val resultado = atualizarCategoriaUseCase(categoria)
            
            resultado.fold(
                onSuccess = {
                    _operacaoSucesso.value = true
                    carregarCategorias()
                },
                onFailure = { e ->
                    _erro.value = e.message ?: "Erro ao atualizar categoria"
                    _operacaoSucesso.value = false
                    _carregando.value = false
                }
            )
        }
    }

    fun limparEstadoOperacao() {
        _operacaoSucesso.value = null
        _erro.value = null
    }
}