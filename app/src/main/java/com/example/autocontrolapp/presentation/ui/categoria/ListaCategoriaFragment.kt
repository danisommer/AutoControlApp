package com.example.autocontrolapp.presentation.ui.categoria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autocontrolapp.R
import com.example.autocontrolapp.databinding.FragmentListaCategoriaBinding
import com.example.autocontrolapp.presentation.adapter.CategoriaAdapter
import com.example.autocontrolapp.presentation.viewmodel.CategoriaViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListaCategoriaFragment : Fragment() {

    private var _binding: FragmentListaCategoriaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriaViewModel by viewModels()
    private lateinit var categoriaAdapter: CategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.carregarCategorias()
    }

    private fun setupRecyclerView() {
        categoriaAdapter = CategoriaAdapter(onItemClick = { categoria ->
            findNavController().navigate(
                ListaCategoriaFragmentDirections
                    .actionListaCategoriaFragmentToDetalheCategoriaFragment(categoria.id)
            )
        })

        binding.recyclerCategorias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriaAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categorias.collectLatest { categorias ->
                categoriaAdapter.submitList(categorias)
                binding.emptyView.visibility = if (categorias.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.carregando.collectLatest { carregando ->
                binding.progressBar.visibility = if (carregando) View.VISIBLE else View.GONE
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
    }

    private fun setupListeners() {
        binding.fabNovaCategoria.setOnClickListener {
            findNavController().navigate(
                ListaCategoriaFragmentDirections
                    .actionListaCategoriaFragmentToNovaCategoriaFragment()
            )
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.carregarCategorias()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}