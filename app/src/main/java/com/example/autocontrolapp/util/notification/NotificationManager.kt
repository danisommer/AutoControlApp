package com.example.autocontrolapp.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.autocontrolapp.R
import com.example.autocontrolapp.domain.model.NotificacaoModel
import com.example.autocontrolapp.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CANAL_ID = "canal_monitorias"
        const val CANAL_NOME = "Monitorias"
        const val CANAL_DESCRICAO = "Notificações sobre monitorias programadas"
    }
    
    init {
        criarCanalNotificacao()
    }
    
    fun exibirNotificacao(notificacao: NotificacaoModel) {
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
            .setSmallIcon(R.drawable.ic_notificacao)
            .setContentTitle(notificacao.titulo)
            .setContentText(notificacao.mensagem)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        // Exibe a notificação
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificacao.id, builder.build())
    }
    
    private fun criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CANAL_ID, CANAL_NOME, importancia).apply {
                description = CANAL_DESCRICAO
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}