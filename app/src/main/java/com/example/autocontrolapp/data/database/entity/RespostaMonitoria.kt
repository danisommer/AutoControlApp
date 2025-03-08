package com.example.autocontrolapp.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "respostas_monitoria",
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
data class RespostaMonitoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monitoriaId: Long,
    val subtopicoId: Long,
    val valorBoolean: Boolean? = null,
    val valorTexto: String? = null,
    val valorNumerico: Double? = null,
    val valorData: Date? = null,
    val valorOpcaoId: Long? = null
)