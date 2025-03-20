package com.example.autocontrolapp.di

import android.content.Context
import androidx.room.Room
import com.example.autocontrolapp.data.database.AppDatabase
import com.example.autocontrolapp.data.database.dao.*
import com.example.autocontrolapp.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "auto_control_app_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCategoriaDao(db: AppDatabase): CategoriaDao = db.categoriaDao()

    @Provides
    @Singleton
    fun provideSubtopicoDao(db: AppDatabase): SubtopicoDao = db.subtopicoDao()

    @Provides
    @Singleton
    fun provideMonitoriaDao(db: AppDatabase): MonitoriaDao = db.monitoriaDao()

    @Provides
    @Singleton
    fun provideRegistroMonitoriaDao(db: AppDatabase): RegistroMonitoriaDao = db.registroMonitoriaDao()

    @Provides
    @Singleton
    fun provideCategoriaRepository(categoriaDao: CategoriaDao): CategoriaRepository {
        return CategoriaRepository(categoriaDao)
    }

    @Provides
    @Singleton
    fun provideSubtopicoRepository(subtopicoDao: SubtopicoDao): SubtopicoRepository {
        return SubtopicoRepository(subtopicoDao)
    }

    @Provides
    @Singleton
    fun provideMonitoriaRepository(monitoriaDao: MonitoriaDao): MonitoriaRepository {
        return MonitoriaRepository(monitoriaDao)
    }

    @Provides
    @Singleton
    fun provideRegistroMonitoriaRepository(registroMonitoriaDao: RegistroMonitoriaDao): RegistroMonitoriaRepository {
        return RegistroMonitoriaRepository(registroMonitoriaDao)
    }
}
