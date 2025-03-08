package com.example.autocontrolapp.util.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.autocontrolapp.domain.model.CategoriaModel
import com.example.autocontrolapp.domain.model.MonitoriaModel
import com.example.autocontrolapp.domain.model.SubtopicoModel
import com.example.autocontrolapp.domain.model.TipoDadoEnum
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class PdfGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PAGE_WIDTH = 595 // A4 width in points (72 dpi)
        private const val PAGE_HEIGHT = 842 // A4 height in points (72 dpi)
        private const val MARGIN = 50f
    }
    
    fun gerarPdfMonitoria(
        categoria: CategoriaModel,
        monitoria: MonitoriaModel,
        subtopicos: List<SubtopicoModel>,
        diretorioSaida: File
    ): File {
        // Preparar dados para o PDF
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dataFormatada = dateFormat.format(monitoria.dataRealizacao)
        val nomeArquivo = "Monitoria_${categoria.nome.replace(" ", "_")}_${dataFormatada.replace("/", "").replace(":", "").replace(" ", "_")}.pdf"
        val arquivo = File(diretorioSaida, nomeArquivo)
        
        // Criar documento PDF
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        
        // Preparar estilos
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }
        
        val subtitlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            isFakeBoldText = true
        }
        
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }
        
        val separatorPaint = Paint().apply {
            color = Color.GRAY
            strokeWidth = 1f
        }
        
        // Desenhar conteúdo
        var yPos = MARGIN + 20f
        
        // Cabeçalho
        canvas.drawText("RELATÓRIO DE MONITORIA", MARGIN, yPos, titlePaint)
        yPos += 30f
        
        canvas.drawText("Categoria: ${categoria.nome}", MARGIN, yPos, subtitlePaint)
        yPos += 20f
        
        canvas.drawText("Data: $dataFormatada", MARGIN, yPos, textPaint)
        yPos += 20f
        
        canvas.drawText("Realizado por: ${monitoria.realizadaPor}", MARGIN, yPos, textPaint)
        yPos += 30f
        
        // Linha separadora
        canvas.drawLine(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos, separatorPaint)
        yPos += 20f
        
        // Observações gerais
        if (monitoria.observacoes.isNotBlank()) {
            canvas.drawText("Observações gerais:", MARGIN, yPos, subtitlePaint)
            yPos += 20f
            
            val observacoesLineHeight = 15f
            val observacoesLines = monitoria.observacoes.split("\n")
            for (linha in observacoesLines) {
                canvas.drawText(linha, MARGIN, yPos, textPaint)
                yPos += observacoesLineHeight
            }
            
            yPos += 10f
            canvas.drawLine(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos, separatorPaint)
            yPos += 20f
        }
        
        // Itens verificados
        canvas.drawText("ITENS VERIFICADOS", MARGIN, yPos, subtitlePaint)
        yPos += 30f
        
        // Cabeçalho da tabela
        val col1 = MARGIN
        val col2 = MARGIN + 250f
        val col3 = PAGE_WIDTH - MARGIN - 100f
        
        canvas.drawText("Item", col1, yPos, subtitlePaint)
        canvas.drawText("Resposta", col2, yPos, subtitlePaint)
        canvas.drawText("Observação", col3, yPos, subtitlePaint)
        yPos += 15f
        
        canvas.drawLine(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos, separatorPaint)
        yPos += 20f
        
        // Itens da monitoria
        val itemHeight = 40f
        
        for (resposta in monitoria.respostas) {
            val subtopico = subtopicos.find { it.id == resposta.subtopicoId }
            if (subtopico != null) {
                val nomeSubtopico = subtopico.nome
                val valorResposta = when (subtopico.tipoDado) {
                    TipoDadoEnum.OK_NAO_OK -> if (resposta.valorResposta == "1") "OK" else "Não OK"
                    else -> resposta.valorResposta
                }
                
                canvas.drawText(nomeSubtopico, col1, yPos, textPaint)
                canvas.drawText(valorResposta, col2, yPos, textPaint)
                canvas.drawText(resposta.observacoes, col3, yPos, textPaint)
                
                yPos += itemHeight
                
                // Se estiver próximo do final da página, criar nova página
                if (yPos > PAGE_HEIGHT - MARGIN) {
                    document.finishPage(page)
                    val newPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, document.pages.size + 1).create()
                    val newPage = document.startPage(newPageInfo)
                    canvas = newPage.canvas
                    yPos = MARGIN + 20f
                }
            }
        }
        
        document.finishPage(page)
        
        // Salvar o PDF
        try {
            FileOutputStream(arquivo).use { out ->
                document.writeTo(out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
        }
        
        return arquivo
    }
}