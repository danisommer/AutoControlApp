package com.example.autocontrolapp.domain.usecase.notificacao

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.autocontrolapp.R
import com.example.autocontrolapp.domain.model.NotificacaoModel
import com.example.autocontrolapp.domain.repository.NotificacaoRepository
import com.example.autocontrolapp.presentation.MainActivity
import javax.inject.Inject

class ExibirNotificacaoUseCase @Inject constructor(
    private val notificacaoRepository: NotificacaoRepository
) {
    suspend operator fun invoke(
        context: Context,
        notificacaoId: Int
    ): Result<Unit> {
        return try {
            val notificacao = notificacaoRepository.obterNotificacaoPorId(notificacaoId)
                ?: return Result.failure(IllegalArgumentException("Notificação não encontrada"))
            
            // Cria o canal de notificação (necessário para Android 8.0+)
            criarCanalNotificacao(context)
            
            // Intent para abrir o app quando clicar na notificação
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("categoria_id", notificacao.categoriaId)
                putExtra("notificacao_id", notificacao.id)
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context, 
                notificacao.id, 
                intent, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            
            // Constrói a notificação
            val builder = NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_notificacao) // Você precisará criar este ícone
                .setContentTitle(notificacao.titulo)
                .setContentText(notificacao.mensagem)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            
            // Exibe a notificação
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificacao.id, builder.build())
            
            // Marca a notificação como visualizada
            notificacaoRepository.marcarComoVisualizada(notificacao.id)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun criarCanalNotificacao(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nome = "Monitorias"
            val descricao = "Notificações sobre monitorias programadas"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CANAL_ID, nome, importancia).apply {
                description = descricao
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
    
    companion object {
        private const val CANAL_ID = "canal_monitorias"
    }
}