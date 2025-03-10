package com.example.autocontrolapp.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registro_monitoria",
    foreignKeys = [
        ForeignKey(
            entity = Monitoria::class,
            parentColumns = ["id"],
            childColumns = ["monitoriaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Subtopico::class,
            parentColumns = ["id"],
            childColumns = ["subtopicoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("monitoriaId"), Index("subtopicoId")]
)
data class RegistroMonitoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monitoriaId: Long,
    val subtopicoId: Long,
    val conforme: Boolean,    // Indica se o item está conforme
    val valor: String? = null // Informação adicional (comentário, número ou opção selecionada)
)
