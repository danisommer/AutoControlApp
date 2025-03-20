package com.example.autocontrolapp.ui.categoria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autocontrolapp.data.database.entity.Categoria
import com.example.autocontrolapp.data.repository.CategoriaRepository
import com.example.autocontrolapp.databinding.FragmentCategoriaBinding
import com.example.autocontrolapp.viewmodel.CategoriaViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.autocontrolapp.di.AppModule
import com.example.autocontrolapp.viewmodel.CategoriaViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class CategoriaFragment : Fragment() {

    private var _binding: FragmentCategoriaBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CategoriaViewModel by viewModels()
    private lateinit var adapter: CategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriaDao = AppModule.provideCategoriaDao(AppModule.provideDatabase(requireContext()))
        val repository = CategoriaRepository(categoriaDao)

        val viewModelFactory = CategoriaViewModelFactory(repository)

        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(CategoriaViewModel::class.java)
    }

    
    private fun setupRecyclerView() {
        adapter = CategoriaAdapter(
            onEdit = { categoria -> showEditCategoriaDialog(categoria) },
            onDelete = { categoria -> showDeleteConfirmationDialog(categoria) }
        )
        
        binding.recyclerViewCategorias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategoriaFragment.adapter
        }
    }
    
    private fun loadCategorias() {
        viewLifecycleOwner.lifecycleScope.launch {
            val categorias = viewModel.getAllCategorias()
            adapter.submitList(categorias)
            
            // Show empty view if list is empty
            binding.emptyView.visibility = if (categorias.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewCategorias.visibility = if (categorias.isEmpty()) View.GONE else View.VISIBLE
        }
    }
    
    private fun showAddCategoriaDialog() {
        val dialogBinding = DialogCategoriaBinding.inflate(layoutInflater)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Adicionar Categoria")
            .setView(dialogBinding.root)
            .setPositiveButton("Salvar") { _, _ ->
                val nome = dialogBinding.etNome.text.toString()
                val descricao = dialogBinding.etDescricao.text.toString()
                val periodicidade = dialogBinding.etPeriodicidade.text.toString().toIntOrNull() ?: 0
                
                val hoje = Calendar.getInstance().time
                val proximaVerificacao = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, periodicidade)
                }.time
                
                val categoria = Categoria(
                    nome = nome,
                    descricao = descricao,
                    periodicidade = periodicidade,
                    ultimaVerificacao = null,
                    proximaVerificacao = proximaVerificacao
                )
                
                viewModel.addCategoria(categoria)
                loadCategorias()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun showEditCategoriaDialog(categoria: Categoria) {
        val dialogBinding = DialogCategoriaBinding.inflate(layoutInflater)
        
        // Preencher os campos com os dados existentes
        dialogBinding.etNome.setText(categoria.nome)
        dialogBinding.etDescricao.setText(categoria.descricao)
        dialogBinding.etPeriodicidade.setText(categoria.periodicidade.toString())
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar Categoria")
            .setView(dialogBinding.root)
            .setPositiveButton("Salvar") { _, _ ->
                val nome = dialogBinding.etNome.text.toString()
                val descricao = dialogBinding.etDescricao.text.toString()
                val periodicidade = dialogBinding.etPeriodicidade.text.toString().toIntOrNull() ?: 0
                
                val hoje = Calendar.getInstance().time
                val proximaVerificacao = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, periodicidade)
                }.time
                
                val categoriaAtualizada = categoria.copy(
                    nome = nome,
                    descricao = descricao,
                    periodicidade = periodicidade,
                    proximaVerificacao = proximaVerificacao
                )
                
                viewModel.updateCategoria(categoriaAtualizada)
                loadCategorias()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun showDeleteConfirmationDialog(categoria: Categoria) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Excluir Categoria")
            .setMessage("Tem certeza que deseja excluir '${categoria.nome}'? Esta ação não pode ser desfeita.")
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.deleteCategoria(categoria)
                loadCategorias()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}