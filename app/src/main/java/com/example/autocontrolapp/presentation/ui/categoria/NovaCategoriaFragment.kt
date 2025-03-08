package com.example.autocontrolapp.presentation.ui.categoria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.autocontrolapp.R
import com.example.autocontrolapp.databinding.FragmentNovaCategoriaBinding
import com.example.autocontrolapp.domain.model.CategoriaModel
import com.example.autocontrolapp.presentation.viewmodel.CategoriaViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NovaCategoriaFragment : Fragment() {

    private var _binding: FragmentNovaCategoriaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNovaCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.carregando.collectLatest { carregando ->
                binding.progressBar.visibility = if (carregando) View.VISIBLE else View.GONE
                binding.btnSalvar.isEnabled = !carregando
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.erro.collectLatest { erro ->
                if (erro != null) {
                    Snackbar.make(binding.root, erro, Snackbar.LENGTH_LONG).show()
                    viewModel.limparEstadoOperacao()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.operacaoSucesso.collectLatest { sucesso ->
                if (sucesso == true) {
                    Snackbar.make(binding.root, "Categoria salva com sucesso", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    viewModel.limparEstadoOperacao()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSalvar.setOnClickListener {
            val nome = binding.editNome.text.toString().trim()
            val descricao = binding.editDescricao.text.toString().trim()
            val periodoText = binding.editPeriodo.text.toString()
            
            if (nome.isEmpty()) {
                binding.editNome.error = "Nome é obrigatório"
                return@setOnClickListener
            }
            
            if (periodoText.isEmpty()) {
                binding.editPeriodo.error = "Período é obrigatório"
                return@setOnClickListener
            }
            
            val periodo = periodoText.toIntOrNull()
            if (periodo == null || periodo <= 0) {
                binding.editPeriodo.error = "Período deve ser um número maior que zero"
                return@setOnClickListener
            }
            
            val categoria = CategoriaModel(
                nome = nome,
                descricao = descricao,
                periodo = periodo
            )
            
            viewModel.adicionarCategoria(categoria)
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}