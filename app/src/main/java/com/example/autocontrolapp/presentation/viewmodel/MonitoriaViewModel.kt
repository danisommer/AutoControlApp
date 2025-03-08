package com.example.autocontrolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocontrolapp.domain.model.MonitoriaModel
import com.example.autocontrolapp.domain.model.RespostaMonitoriaModel
import com.example.autocontrolapp.domain.model.SubtopicoModel
import com.example.autocontrolapp.domain.usecase.monitoria.ListarMonitoriasUseCase
import com.example.autocontrolapp.domain.usecase.monitoria.RealizarMonitoriaUseCase
import com.example.autocontrolapp.domain.usecase.subtopico.ListarSubtopicoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MonitoriaViewModel @Inject constructor(
    private val realizarMonitoriaUseCase: RealizarMonitoriaUseCase,
    private val listarMonitoriasUseCase: ListarMonitoriasUseCase,
    private val listarSubtopicoUseCase: ListarSubtopicoUseCase
) : ViewModel() {

    private val _monitorias = MutableStateFlow<List<MonitoriaModel>>(emptyList())
    val monitorias: StateFlow<List<MonitoriaModel>> = _monitorias.asStateFlow()

    private val _subtopicosCategoria = MutableStateFlow<List<SubtopicoModel>>(emptyList())
    val subtopicosCategoria: StateFlow<List<SubtopicoModel>> = _subtopicosCategoria.asStateFlow()

    private val _respostas = MutableStateFlow<Map<Int, String>>(emptyMap())
    val respostas: StateFlow<Map<Int, String>> = _respostas.asStateFlow()

    private val _observacoesResposta = MutableStateFlow<Map<Int, String>>(emptyMap())
    val observacoesResposta: StateFlow<Map<Int, String>> = _observacoesResposta.asStateFlow()

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    private val _operacaoSucesso = MutableStateFlow<Boolean?>(null)
    val operacaoSucesso: StateFlow<Boolean?> = _operacaoSucesso.asStateFlow()

    private val _realizadaPor = MutableStateFlow("")
    val realizadaPor: StateFlow<String> = _realizadaPor.asStateFlow()

    private val _observacoesGerais = MutableStateFlow("")
    val observacoesGerais: StateFlow<String> = _observacoesGerais.asStateFlow()

    fun carregarMonitoriasPorCategoria(categoriaId: Int) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            
            listarMonitoriasUseCase.obterMonitoriasPorCategoria(categoriaId)
                .catch { e ->
                    _erro.value = e.message ?: "Erro ao carregar monitorias"
                    _carregando.value = false
                }
                .collectLatest { lista ->
                    _monitorias.value = lista
                    _carregando.value = false
                }
        }
    }

    fun carregarSubtopicosDaCategoria(categoriaId: Int) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            
            listarSubtopicoUseCase.obterPorCategoria(categoriaId)
                .catch { e ->
                    _erro.value = e.message ?: "Erro ao carregar subtópicos"
                    _carregando.value = false
                }
                .collectLatest { lista ->
                    _subtopicosCategoria.value = lista
                    _carregando.value = false
                }
        }
    }
    
    fun iniciarNovaMonitoria() {
        _respostas.value = emptyMap()
        _observacoesResposta.value = emptyMap()
        _realizadaPor.value = ""
        _observacoesGerais.value = ""
        _operacaoSucesso.value = null
    }
    
    fun setRealizadaPor(nome: String) {
        _realizadaPor.value = nome
    }
    
    fun setObservacoesGerais(obs: String) {
        _observacoesGerais.value = obs
    }
    
    fun registrarResposta(subtopicoId: Int, valor: String) {
        val novoMap = _respostas.value.toMutableMap()
        novoMap[subtopicoId] = valor
        _respostas.value = novoMap
    }
    
    fun registrarObservacaoResposta(subtopicoId: Int, observacao: String) {
        val novoMap = _observacoesResposta.value.toMutableMap()
        novoMap[subtopicoId] = observacao
        _observacoesResposta.value = novoMap
    }
    
    fun verificarRespostasCompletas(): Boolean {
        val subtopicosObrigatorios = _subtopicosCategoria.value.filter { it.obrigatorio }
        return subtopicosObrigatorios.all { _respostas.value.containsKey(it.id) }
    }
    
    fun salvarMonitoria(categoriaId: Int) {
        if (_realizadaPor.value.isBlank()) {
            _erro.value = "É necessário informar quem está realizando a monitoria"
            return
        }
        
        if (!verificarRespostasCompletas()) {
            _erro.value = "É necessário responder todos os itens obrigatórios"
            return
        }
        
        val respostasLista = _respostas.value.map { (subtopicoId, valor) ->
            RespostaMonitoriaModel(
                subtopicoId = subtopicoId,
                valorResposta = valor,
                observacoes = _observacoesResposta.value[subtopicoId] ?: ""
            )
        }
        
        val monitoria = MonitoriaModel(
            categoriaId = categoriaId,
            dataRealizacao = Date(),
            realizadaPor = _realizadaPor.value,
            observacoes = _observacoesGerais.value,
            respostas = respostasLista
        )
        
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            
            val resultado = realizarMonitoriaUseCase(monitoria)
            
            resultado.fold(
                onSuccess = {
                    _operacaoSucesso.value = true
                    carregarMonitoriasPorCategoria(categoriaId)
                },
                onFailure = { e ->
                    _erro.value = e.message ?: "Erro ao salvar monitoria"
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