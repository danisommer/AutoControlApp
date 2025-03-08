package com.example.autocontrolapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autocontrolapp.databinding.ItemMonitoriaBinding
import com.example.autocontrolapp.domain.model.MonitoriaModel
import java.text.SimpleDateFormat
import java.util.Locale

class MonitoriaAdapter(
    private val onItemClick: (MonitoriaModel) -> Unit
) : ListAdapter<MonitoriaModel, MonitoriaAdapter.MonitoriaViewHolder>(MonitoriaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitoriaViewHolder {
        val binding = ItemMonitoriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MonitoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonitoriaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MonitoriaViewHolder(
        private val binding: ItemMonitoriaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(monitoria: MonitoriaModel) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dataFormatada = dateFormat.format(monitoria.dataRealizacao)
            
            binding.tvDataMonitoria.text = dataFormatada
            binding.tvRealizadaPor.text = "Realizada por: ${monitoria.realizadaPor}"
            
            if (monitoria.observacoes.isNotBlank()) {
                binding.tvObservacoes.text = monitoria.observacoes
                binding.tvObservacoes.visibility = View.VISIBLE
            } else {
                binding.tvObservacoes.visibility = View.GONE
            }
            
            binding.tvQuantidadeRespostas.text = "${monitoria.respostas.size} itens verificados"
            
            // Verifica se existem itens não conforme
            val itensNaoConforme = monitoria.respostas.count { 
                it.valorResposta.equals("Não OK", ignoreCase = true) || 
                it.valorResposta == "0"
            }
            
            if (itensNaoConforme > 0) {
                binding.chipStatusMonitoria.text = "$itensNaoConforme não conforme"
                binding.chipStatusMonitoria.isChecked = false
            } else {
                binding.chipStatusMonitoria.text = "Tudo OK"
                binding.chipStatusMonitoria.isChecked = true
            }
        }
    }

    class MonitoriaDiffCallback : DiffUtil.ItemCallback<MonitoriaModel>() {
        override fun areItemsTheSame(oldItem: MonitoriaModel, newItem: MonitoriaModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MonitoriaModel, newItem: MonitoriaModel): Boolean {
            return oldItem == newItem
        }
    }
}