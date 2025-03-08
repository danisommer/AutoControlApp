package com.example.autocontrolapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autocontrolapp.databinding.ItemSubtopicoBinding
import com.example.autocontrolapp.domain.model.SubtopicoModel
import com.example.autocontrolapp.domain.model.TipoDadoEnum

class SubtopicoAdapter(
    private val onItemClick: ((SubtopicoModel) -> Unit)? = null,
    private val onEditClick: ((SubtopicoModel) -> Unit)? = null
) : ListAdapter<SubtopicoModel, SubtopicoAdapter.SubtopicoViewHolder>(SubtopicoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtopicoViewHolder {
        val binding = ItemSubtopicoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubtopicoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubtopicoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SubtopicoViewHolder(
        private val binding: ItemSubtopicoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position))
                }
            }
            
            binding.btnEditarSubtopico.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick?.invoke(getItem(position))
                }
            }
        }

        fun bind(subtopico: SubtopicoModel) {
            binding.tvNomeSubtopico.text = subtopico.nome
            
            if (subtopico.descricao.isNotBlank()) {
                binding.tvDescricaoSubtopico.text = subtopico.descricao
                binding.tvDescricaoSubtopico.visibility = View.VISIBLE
            } else {
                binding.tvDescricaoSubtopico.visibility = View.GONE
            }
            
            val tipoTexto = when (subtopico.tipoDado) {
                TipoDadoEnum.OK_NAO_OK -> "OK/Não OK"
                TipoDadoEnum.TEXTO -> "Texto"
                TipoDadoEnum.NUMERO -> "Número"
                TipoDadoEnum.SELECAO -> "Seleção"
            }
            binding.tvTipoDadoSubtopico.text = "Tipo: $tipoTexto"
            
            binding.chipObrigatorio.visibility = if (subtopico.obrigatorio) View.VISIBLE else View.GONE
            
            // Exibe quantidade de opções se for do tipo seleção
            if (subtopico.tipoDado == TipoDadoEnum.SELECAO && subtopico.valoresOpcoes.isNotEmpty()) {
                binding.tvOpcoesSubtopico.visibility = View.VISIBLE
                binding.tvOpcoesSubtopico.text = "${subtopico.valoresOpcoes.size} opções"
            } else {
                binding.tvOpcoesSubtopico.visibility = View.GONE
            }
        }
    }

    class SubtopicoDiffCallback : DiffUtil.ItemCallback<SubtopicoModel>() {
        override fun areItemsTheSame(oldItem: SubtopicoModel, newItem: SubtopicoModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubtopicoModel, newItem: SubtopicoModel): Boolean {
            return oldItem == newItem
        }
    }
}