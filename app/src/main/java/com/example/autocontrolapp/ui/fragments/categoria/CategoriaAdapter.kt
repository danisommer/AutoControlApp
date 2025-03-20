package com.example.autocontrolapp.ui.categoria

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autocontrolapp.R
import com.example.autocontrolapp.data.database.entity.Categoria
import com.example.autocontrolapp.databinding.ItemCategoriaBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CategoriaAdapter(
    private val onEdit: (Categoria) -> Unit,
    private val onDelete: (Categoria) -> Unit
) : ListAdapter<Categoria, CategoriaAdapter.CategoriaViewHolder>(CategoriaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(getItem(position), onEdit, onDelete)
    }

    class CategoriaViewHolder(
        private val binding: ItemCategoriaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoria: Categoria, onEdit: (Categoria) -> Unit, onDelete: (Categoria) -> Unit) {
            binding.tvNome.text = categoria.nome
            binding.tvDescricao.text = categoria.descricao
            binding.tvPeriodicidade.text = "A cada ${categoria.periodicidade} dias"

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvProximaVerificacao.text = categoria.proximaVerificacao?.let { 
                "Próxima: ${dateFormat.format(it)}" 
            } ?: "Não agendada"

            binding.btnMore.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.inflate(R.menu.menu_categoria_item)
                
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit -> {
                            onEdit(categoria)
                            true
                        }
                        R.id.menu_delete -> {
                            onDelete(categoria)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    private class CategoriaDiffCallback : DiffUtil.ItemCallback<Categoria>() {
        override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem == newItem
        }
    }
}