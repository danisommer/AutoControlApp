package com.example.autocontrolapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "monitorias")
data class Monitoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val categoriaId: Long,
    val data: Date
)
