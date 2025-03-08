package com.example.autocontrolapp.util.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.autocontrolapp.domain.model.NotificacaoModel
import com.example.autocontrolapp.domain.repository.NotificacaoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationManager: NotificationManagerImpl,
    private val notificacaoRepository: NotificacaoRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val notificacaoId = inputData.getInt("notificacao_id", -1)
                
                // Se tiver ID específico, exibe essa notificação
                if (notificacaoId > 0) {
                    val notificacao = notificacaoRepository.obterNotificacaoPorId(notificacaoId)
                    if (notificacao != null) {
                        notificationManager.exibirNotificacao(notificacao)
                        // Marca como visualizada no banco de dados
                        notificacaoRepository.marcarComoVisualizada(notificacaoId)
                        return@withContext Result.success()
                    }
                    return@withContext Result.failure()
                }
                
                // Caso contrário, verifica notificações pendentes para a data atual
                val categoriaId = inputData.getInt("categoria_id", -1)
                val titulo = inputData.getString("titulo") ?: "Verificação pendente"
                val mensagem = inputData.getString("mensagem") ?: "É hora de realizar uma verificação"
                
                if (categoriaId > 0) {
                    // Cria a notificação
                    val notificacao = NotificacaoModel(
                        categoriaId = categoriaId,
                        titulo = titulo,
                        mensagem = mensagem,
                        dataNotificacao = Date()
                    )
                    
                    val novoId = notificacaoRepository.inserirNotificacao(notificacao)
                    notificationManager.exibirNotificacao(notificacao.copy(id = novoId.toInt()))
                    
                    Result.success()
                } else {
                    Result.failure()
                }
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}