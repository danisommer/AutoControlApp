package com.example.autocontrolapp.domain.usecase.notificacao

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.autocontrolapp.domain.model.CategoriaModel
import com.example.autocontrolapp.domain.model.NotificacaoModel
import com.example.autocontrolapp.domain.repository.NotificacaoRepository
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AgendarNotificacaoUseCase @Inject constructor(
    private val notificacaoRepository: NotificacaoRepository
) {
    suspend operator fun invoke(
        context: Context,
        categoria: CategoriaModel
    ): Result<NotificacaoModel> {
        return try {
            // Verifica se a categoria tem data de próxima verificação
            val dataNotificacao = categoria.proximaVerificacao
                ?: return Result.failure(IllegalArgumentException("Categoria não tem data de próxima verificação"))
            
            // Cria a notificação no banco de dados
            val notificacao = NotificacaoModel(
                categoriaId = categoria.id,
                titulo = "Verificação pendente",
                mensagem = "É hora de realizar a verificação de ${categoria.nome}",
                dataNotificacao = dataNotificacao
            )
            
            val notificacaoId = notificacaoRepository.inserirNotificacao(notificacao)
            
            // Calcula o delay até a notificação
            val agora = Calendar.getInstance()
            val dataAgendamento = Calendar.getInstance()
            dataAgendamento.time = dataNotificacao
            
            val delayMillis = dataAgendamento.timeInMillis - agora.timeInMillis
            
            // Só agenda se a data estiver no futuro
            if (delayMillis > 0) {
                // Configura os dados da notificação para o WorkManager
                val inputData = Data.Builder()
                    .putInt("notificacao_id", notificacaoId.toInt())
                    .putInt("categoria_id", categoria.id)
                    .putString("titulo", notificacao.titulo)
                    .putString("mensagem", notificacao.mensagem)
                    .build()
                
                // Cria a solicitação para o WorkManager
                val notificacaoRequest = OneTimeWorkRequestBuilder<NotificacaoWorker>()
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag("notificacao_${categoria.id}")
                    .build()
                
                // Agenda a notificação
                WorkManager.getInstance(context)
                    .enqueueUniqueWork(
                        "notificacao_categoria_${categoria.id}",
                        ExistingWorkPolicy.REPLACE,
                        notificacaoRequest
                    )
            }
            
            Result.success(notificacao.copy(id = notificacaoId.toInt()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}