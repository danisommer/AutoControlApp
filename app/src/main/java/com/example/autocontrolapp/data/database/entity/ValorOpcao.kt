package com.example.autocontrolapp.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "valores_opcao",
    foreignKeys = [
        ForeignKey(
            entity = Subtopico::class,
            parentColumns = ["id"],
            childColumns = ["subtopicoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subtopicoId")]
)
data class ValorOpcao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subtopicoId: Long,
    val valor: String
)
