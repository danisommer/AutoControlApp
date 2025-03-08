package com.example.autocontrolapp.util.pdf

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.autocontrolapp.domain.model.CategoriaModel
import com.example.autocontrolapp.domain.model.MonitoriaModel
import com.example.autocontrolapp.domain.model.SubtopicoModel
import com.example.autocontrolapp.domain.repository.CategoriaRepository
import com.example.autocontrolapp.domain.repository.MonitoriaRepository
import com.example.autocontrolapp.domain.repository.SubtopicoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfGenerator: PdfGenerator,
    private val categoriaRepository: CategoriaRepository,
    private val monitoriaRepository: MonitoriaRepository,
    private val subtopicoRepository: SubtopicoRepository
) {
    /**
     * Gera um PDF para uma monitoria específica
     */
    suspend fun gerarRelatorioMonitoria(monitoriaId: Int): Result<File> {
        return try {
            // Obter dados necessários
            val monitoria = monitoriaRepository.obterMonitoriaPorId(monitoriaId)
                ?: return Result.failure(IllegalArgumentException("Monitoria não encontrada"))
                
            val categoria = categoriaRepository.obterCategoriaPorId(monitoria.categoriaId)
                ?: return Result.failure(IllegalArgumentException("Categoria não encontrada"))
                
            val subtopicosIds = monitoria.respostas.map { it.subtopicoId }
            val subtopicos = subtopicoRepository.obterSubtopicosPorIds(subtopicosIds)
            
            // Criar diretório para relatórios se não existir
            val diretorio = obterDiretorioRelatorios()
            
            // Gerar o PDF
            val arquivoPdf = pdfGenerator.gerarPdfMonitoria(
                categoria,
                monitoria,
                subtopicos,
                diretorio
            )
            
            Result.success(arquivoPdf)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Compartilha um arquivo PDF usando intent
     */
    fun compartilharPdf(arquivoPdf: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            arquivoPdf
        )
        
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    
    /**
     * Abre o arquivo PDF em um visualizador externo
     */
    fun abrirPdf(arquivoPdf: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            arquivoPdf
        )
        
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    
    /**
     * Lista todos os relatórios disponíveis para uma categoria
     */
    fun listarRelatoriosPorCategoria(categoriaId: Int): List<File> {
        val diretorio = obterDiretorioRelatorios()
        val categoria = runCatching { 
            categoriaRepository.obterCategoriaPorIdSincrono(categoriaId)
        }.getOrNull()
        
        return if (categoria != null) {
            val nomePrefixo = "Monitoria_${categoria.nome.replace(" ", "_")}"
            diretorio.listFiles { file ->
                file.name.startsWith(nomePrefixo) && file.extension.equals("pdf", ignoreCase = true)
            }?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    /**
     * Obter ou criar o diretório para salvar os relatórios
     */
    private fun obterDiretorioRelatorios(): File {
        val diretorio = File(context.getExternalFilesDir(null), "relatorios")
        if (!diretorio.exists()) {
            diretorio.mkdirs()
        }
        return diretorio
    }
}