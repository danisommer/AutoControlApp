package com.example.autocontrolapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.autocontrolapp.data.database.converter.Converters
import com.example.autocontrolapp.data.database.dao.CategoriaDao
import com.example.autocontrolapp.data.database.dao.MonitoriaDao
import com.example.autocontrolapp.data.database.dao.RegistroMonitoriaDao
import com.example.autocontrolapp.data.database.dao.SubtopicoDao
import com.example.autocontrolapp.data.database.entity.Categoria
import com.example.autocontrolapp.data.database.entity.Monitoria
import com.example.autocontrolapp.data.database.entity.RegistroMonitoria
import com.example.autocontrolapp.data.database.entity.Subtopico

@Database(
    entities = [Categoria::class, Subtopico::class, Monitoria::class, RegistroMonitoria::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun subtopicoDao(): SubtopicoDao
    abstract fun monitoriaDao(): MonitoriaDao
    abstract fun registroMonitoriaDao(): RegistroMonitoriaDao
}
