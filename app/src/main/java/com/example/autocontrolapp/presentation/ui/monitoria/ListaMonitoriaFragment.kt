package com.example.autocontrolapp.presentation.ui.monitoria

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
import com.example.autocontrolapp.databinding.FragmentListaMonitoriaBinding
import com.example.autocontrolapp.presentation.adapter.MonitoriaAdapter
import com.example.autocontrolapp.presentation.ui.categoria.DetalheCategoriaFragmentDirections
import com.example.autocontrolapp.presentation.viewmodel.MonitoriaViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListaMonitoriaFragment : Fragment() {

    private var _binding: FragmentListaMonitoriaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MonitoriaViewModel by viewModels()
    private lateinit var monitoriaAdapter: MonitoriaAdapter
    
    private var categoriaId = 0

    companion object {
        private const val ARG_CATEGORIA_ID = "categoria_id"

        fun newInstance(categoriaId: Int): ListaMonitoriaFragment {
            val fragment = ListaMonitoriaFragment()
            val args = Bundle()
            args.putInt(ARG_CATEGORIA_ID, categoriaId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoriaId = it.getInt(ARG_CATEGORIA_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaMonitoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.carregarMonitoriasPorCategoria(categoriaId)
    }

    private fun setupRecyclerView() {
        monitoriaAdapter = MonitoriaAdapter(onItemClick = { monitoria ->
            findNavController().navigate(
                DetalheCategoriaFragmentDirections
                    .actionDetalheCategoriaFragmentToDetalheMonitoriaFragment(monitoria.id)
            )
        })

        binding.recyclerMonitorias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = monitoriaAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.monitorias.collectLatest { monitorias ->
                monitoriaAdapter.submitList(monitorias)
                binding.emptyView.visibility = if (monitorias.isEmpty()) View.VISIBLE else View.GONE
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
        binding.fabNovaMonitoria.setOnClickListener {
            findNavController().navigate(
                DetalheCategoriaFragmentDirections
                    .actionDetalheCategoriaFragmentToNovaMonitoriaFragment(categoriaId)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}