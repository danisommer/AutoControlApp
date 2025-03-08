package com.example.autocontrolapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.autocontrolapp.data.database.dao.*
import com.example.autocontrolapp.data.database.entity.*

@Database(
    entities = [
        Categoria::class,
        Subtopico::class,
        ValorOpcao::class,
        Monitoria::class,
        RespostaMonitoria::class,
        Notificacao::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun subtopicoDao(): SubtopicoDao
    abstract fun valorOpcaoDao(): ValorOpcaoDao
    abstract fun monitoriaDao(): MonitoriaDao
    abstract fun respostaMonitoriaDao(): RespostaMonitoriaDao
    abstract fun notificacaoDao(): NotificacaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autocontrol_database"
                )
                    .fallbackToDestructiveMigration() // Em ambiente de produção, substitua por estratégia de migração adequada
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}