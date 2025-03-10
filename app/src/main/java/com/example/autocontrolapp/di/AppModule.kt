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
    fun provideCategoriaDao(db: AppDatabase): CategoriaDao = db.categoriaDao()

    @Provides
    fun provideSubtopicoDao(db: AppDatabase): SubtopicoDao = db.subtopicoDao()

    @Provides
    fun provideMonitoriaDao(db: AppDatabase): MonitoriaDao = db.monitoriaDao()

    @Provides
    fun provideRegistroMonitoriaDao(db: AppDatabase): RegistroMonitoriaDao = db.registroMonitoriaDao()

    @Provides
    fun provideCategoriaRepository(dao: CategoriaDao): CategoriaRepository = CategoriaRepository(dao)

    @Provides
    fun provideSubtopicoRepository(dao: SubtopicoDao): SubtopicoRepository = SubtopicoRepository(dao)

    @Provides
    fun provideMonitoriaRepository(dao: MonitoriaDao): MonitoriaRepository = MonitoriaRepository(dao)

    @Provides
    fun provideRegistroMonitoriaRepository(dao: RegistroMonitoriaDao): RegistroMonitoriaRepository =
        RegistroMonitoriaRepository(dao)
}
