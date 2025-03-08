package com.example.autocontrolapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocontrolapp.domain.model.MonitoriaModel
import com.example.autocontrolapp.domain.usecase.monitoria.GerarRelatorioUseCase
import com.example.autocontrolapp.domain.usecase.monitoria.ListarMonitoriasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RelatorioViewModel @Inject constructor(
    private val gerarRelatorioUseCase: GerarRelatorioUseCase,
    private val listarMonitoriasUseCase: ListarMonitoriasUseCase
) : ViewModel() {

    private val _monitoriasSemRelatorio = MutableStateFlow<List<MonitoriaModel>>(emptyList())
    val monitoriasSemRelatorio: StateFlow<List<MonitoriaModel>> = _monitoriasSemRelatorio.asStateFlow()

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    private val _arquivoRelatorio = MutableStateFlow<File?>(null)
    val arquivoRelatorio: StateFlow<File?> = _arquivoRelatorio.asStateFlow()

    fun carregarMonitoriasSemRelatorio(categoriaId: Int) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            
            try {
                val monitorias = listarMonitoriasUseCase.obterMonitoriasPorCategoria(categoriaId).first()
                // Aqui assumimos que existe alguma propriedade na monitoria para indicar se já tem relatório gerado
                // Esta lógica precisa ser adaptada de acordo com sua implementação
                _monitoriasSemRelatorio.value = monitorias.filter { 
                    // Implemente a lógica para filtrar monitorias sem relatório
                    true // Temporário, adapte conforme necessário
                }
                _carregando.value = false
            } catch (e: Exception) {
                _erro.value = e.message ?: "Erro ao carregar monitorias"
                _carregando.value = false
            }
        }
    }

    fun gerarRelatorio(context: Context, monitoriaId: Int) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            _arquivoRelatorio.value = null
            
            val resultado = gerarRelatorioUseCase(context, monitoriaId)
            
            resultado.fold(
                onSuccess = { arquivo ->
                    _arquivoRelatorio.value = arquivo
                },
                onFailure = { e ->
                    _erro.value = e.message ?: "Erro ao gerar relatório"
                }
            )
            
            _carregando.value = false
        }
    }

    fun compartilharRelatorio(context: Context) {
        val arquivo = _arquivoRelatorio.value ?: return
        
        // Aqui você implementaria o compartilhamento do arquivo usando Intent
        // Exemplo de uso (pseudocódigo):
        /*
        val intent = Intent(Intent.ACTION_SEND)
        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            arquivo
        )
        intent.setType("application/pdf")
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Compartilhar relatório"))
        */
    }

    fun limparEstadoOperacao() {
        _erro.value = null
        _arquivoRelatorio.value = null
    }
}