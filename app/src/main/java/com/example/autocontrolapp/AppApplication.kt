package com.example.autocontrolapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.autocontrolapp.util.notification.NotificationManagerImpl
import com.example.autocontrolapp.util.verification.VerificationCheckWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class AppApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationManager: NotificationManagerImpl

    override fun onCreate() {
        super.onCreate()
        
        // Inicializa o serviço de verificação diária
        setupPeriodicWorkRequest()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    /**
     * Configura uma verificação diária para monitorias pendentes
     */
    private fun setupPeriodicWorkRequest() {
        val verificacaoDiariaRequest = PeriodicWorkRequestBuilder<VerificationCheckWorker>(
            24, TimeUnit.HOURS // Executa a cada 24 horas
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "verificacao_diaria",
            ExistingPeriodicWorkPolicy.KEEP, // Mantém a existente se já estiver agendada
            verificacaoDiariaRequest
        )
    }
}