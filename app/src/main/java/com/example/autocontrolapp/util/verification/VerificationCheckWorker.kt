package com.example.autocontrolapp.util.verification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.autocontrolapp.domain.model.NotificacaoModel
import com.example.autocontrolapp.domain.repository.CategoriaRepository
import com.example.autocontrolapp.domain.repository.NotificacaoRepository
import com.example.autocontrolapp.util.notification.NotificationManagerImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

@HiltWorker
class VerificationCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val categoriaRepository: CategoriaRepository,
    private val notificacaoRepository: NotificacaoRepository,
    private val notificationManager: NotificationManagerImpl
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val hoje = Date()
            val categoriasParaVerificar = categoriaRepository.obterCategoriasParaVerificacaoHoje(hoje)
            
            for (categoria in categoriasParaVerificar) {
                // Cria uma notificação para cada categoria que precisa ser verificada hoje
                val notificacao = NotificacaoModel(
                    categoriaId = categoria.id,
                    titulo = "Verificação pendente: ${categoria.nome}",
                    mensagem = "É necessário realizar a verificação de ${categoria.nome} hoje",
                    dataNotificacao = hoje
                )
                
                val notificacaoId = notificacaoRepository.inserirNotificacao(notificacao)
                
                // Exibe a notificação
                notificationManager.exibirNotificacao(notificacao.copy(id = notificacaoId.toInt()))
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}