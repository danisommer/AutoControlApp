package com.example.autocontrolapp.ui.categoria

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.autocontrolapp.databinding.DialogCategoriaBinding

class DialogCategoriaBinding private constructor(
    val root: android.view.View,
    val etNome: com.google.android.material.textfield.TextInputEditText,
    val etDescricao: com.google.android.material.textfield.TextInputEditText,
    val etPeriodicidade: com.google.android.material.textfield.TextInputEditText
) {
    companion object {
        fun inflate(
            inflater: LayoutInflater,
            parent: ViewGroup? = null,
            attachToParent: Boolean = false
        ): com.example.autocontrolapp.ui.categoria.DialogCategoriaBinding {
            val binding = com.example.autocontrolapp.databinding.DialogCategoriaBinding.inflate(inflater, parent, attachToParent)
            return DialogCategoriaBinding(
                binding.root,
                binding.etNome,
                binding.etDescricao,
                binding.etPeriodicidade
            )
        }
    }
}