package com.example.autocontrolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocontrolapp.domain.model.SubtopicoModel
import com.example.autocontrolapp.domain.model.TipoDadoEnum
import com.example.autocontrolapp.domain.model.ValorOpcaoModel
import com.example.autocontrolapp.domain.usecase.subtopico.AdicionarSubtopicoUseCase
import com.example.autocontrolapp.domain.usecase.subtopico.ListarSubtopicoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubtopicoViewModel @Inject constructor(
    private val listarSubtopicoUseCase: ListarSubtopicoUseCase,
    private val adicionarSubtopicoUseCase: AdicionarSubtopicoUseCase
) : ViewModel() {

    private val _subtopicos = MutableStateFlow<List<SubtopicoModel>>(emptyList())
    val subtopicos: StateFlow<List<SubtopicoModel>> = _subtopicos.asStateFlow()

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    private val _operacaoSucesso = MutableStateFlow<Boolean?>(null)
    val operacaoSucesso: StateFlow<Boolean?> = _operacaoSucesso.asStateFlow()
    
    private val _novoSubtopico = MutableStateFlow<SubtopicoModel?>(null)
    val novoSubtopico: StateFlow<SubtopicoModel?> = _novoSubtopico.asStateFlow()
    
    private val _valoresOpcoes = MutableStateFlow<List<ValorOpcaoModel>>(emptyList())
    val valoresOpcoes: StateFlow<List<ValorOpcaoModel>> = _valoresOpcoes.asStateFlow()

    fun carregarSubtopicosPorCategoria(categoriaId: Int) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            
            listarSubtopicoUseCase.obterPorCategoria(categoriaId)
                .catch { e ->
                    _erro.value = e.message ?: "Erro ao carregar subtópicos"
                    _carregando.value = false
                }
                .collectLatest { lista ->
                    _subtopicos.value = lista
                    _carregando.value = false
                }
        }
    }

    fun iniciarNovoSubtopico(categoriaId: Int) {
        _novoSubtopico.value = SubtopicoModel(
            categoriaId = categoriaId,
            nome = "",
            tipoDado = TipoDadoEnum.OK_NAO_OK
        )
        _valoresOpcoes.value = emptyList()
    }
    
    fun atualizarNovoSubtopico(subtopico: SubtopicoModel) {
        _novoSubtopico.value = subtopico
    }
    
    fun adicionarValorOpcao(valor: String) {
        val novaLista = _valoresOpcoes.value.toMutableList()
        novaLista.add(
            ValorOpcaoModel(
                subtopicoId = _novoSubtopico.value?.id ?: 0,
                valor = valor,
                ordem = novaLista.size
            )
        )
        _valoresOpcoes.value = novaLista
    }
    
    fun removerValorOpcao(index: Int) {
        val novaLista = _valoresOpcoes.value.toMutableList()
        if (index in novaLista.indices) {
            novaLista.removeAt(index)
            // Atualiza a ordem
            novaLista.forEachIndexed { i, valor ->
                if (i != valor.ordem) {
                    novaLista[i] = valor.copy(ordem = i)
                }
            }
            _valoresOpcoes.value = novaLista
        }
    }

    fun salvarSubtopico() {
        val subtopico = _novoSubtopico.value ?: return
        
        if (subtopico.tipoDado == TipoDadoEnum.SELECAO && _valoresOpcoes.value.isEmpty()) {
            _erro.value = "É necessário adicionar pelo menos uma opção"
            return
        }
        
        val subtopicoCompleto = subtopico.copy(valoresOpcoes = _valoresOpcoes.value)
        
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            _operacaoSucesso.value = null
            
            val resultado = adicionarSubtopicoUseCase(subtopicoCompleto)
            
            resultado.fold(
                onSuccess = {
                    _operacaoSucesso.value = true
                    _novoSubtopico.value = null
                    _valoresOpcoes.value = emptyList()
                    carregarSubtopicosPorCategoria(subtopico.categoriaId)
                },
                onFailure = { e ->
                    _erro.value = e.message ?: "Erro ao adicionar subtópico"
                    _operacaoSucesso.value = false
                }
            )
            
            _carregando.value = false
        }
    }

    fun limparEstadoOperacao() {
        _operacaoSucesso.value = null
        _erro.value = null
    }
}