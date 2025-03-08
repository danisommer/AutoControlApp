package com.example.autocontrolapp.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subtopicos",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoriaId")]
)
data class Subtopico(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoriaId: Long,
    val nome: String,
    val descricao: String,
    val tipoDado: TipoDado
)