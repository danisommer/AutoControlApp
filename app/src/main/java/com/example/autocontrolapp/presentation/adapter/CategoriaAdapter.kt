package com.example.autocontrolapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autocontrolapp.databinding.ItemCategoriaBinding
import com.example.autocontrolapp.domain.model.CategoriaModel
import java.text.SimpleDateFormat
import java.util.Locale

class CategoriaAdapter(
    private val onItemClick: (CategoriaModel) -> Unit
) : ListAdapter<CategoriaModel, CategoriaAdapter.CategoriaViewHolder>(CategoriaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoriaViewHolder(
        private val binding: ItemCategoriaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(categoria: CategoriaModel) {
            binding.tvNomeCategoria.text = categoria.nome
            binding.tvDescricaoCategoria.text = 
                if (categoria.descricao.isBlank()) "Sem descrição" else categoria.descricao
            binding.tvPeriodoCategoria.text = "A cada ${categoria.periodo} dias"
            
            val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            
            val textoProximaVerificacao = categoria.proximaVerificacao?.let { 
                formatoData.format(it) 
            } ?: "Não agendado"
            
            binding.tvProximaVerificacao.text = "Próxima: $textoProximaVerificacao"
            
            binding.chipStatus.text = if (categoria.ativa) "Ativa" else "Inativa"
            binding.chipStatus.isChecked = categoria.ativa
        }
    }

    class CategoriaDiffCallback : DiffUtil.ItemCallback<CategoriaModel>() {
        override fun areItemsTheSame(oldItem: CategoriaModel, newItem: CategoriaModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoriaModel, newItem: CategoriaModel): Boolean {
            return oldItem == newItem
        }
    }
}