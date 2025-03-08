package com.example.autocontrolapp.presentation.ui.relatorio

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
import com.example.autocontrolapp.databinding.FragmentListaRelatorioBinding
import com.example.autocontrolapp.presentation.adapter.RelatorioAdapter
import com.example.autocontrolapp.presentation.ui.categoria.DetalheCategoriaFragmentDirections
import com.example.autocontrolapp.presentation.viewmodel.RelatorioViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ListaRelatorioFragment : Fragment() {

    private var _binding: FragmentListaRelatorioBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RelatorioViewModel by viewModels()
    private lateinit var relatorioAdapter: RelatorioAdapter
    
    private var categoriaId = 0

    companion object {
        private const val ARG_CATEGORIA_ID = "categoria_id"

        fun newInstance(categoriaId: Int): ListaRelatorioFragment {
            val fragment = ListaRelatorioFragment()
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
        _binding = FragmentListaRelatorioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        
        // Carrega as monitorias que ainda não têm relatório gerado
        viewModel.carregarMonitoriasSemRelatorio(categoriaId)
    }

    private fun setupRecyclerView() {
        relatorioAdapter = RelatorioAdapter(
            onItemClick = { monitoriaId ->
                // Gera relatório para a monitoria selecionada
                viewModel.gerarRelatorio(requireContext(), monitoriaId)
            }
        )

        binding.recyclerRelatorios.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = relatorioAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.monitoriasSemRelatorio.collectLatest { monitorias ->
                relatorioAdapter.submitList(monitorias)
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
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.arquivoRelatorio.collectLatest { arquivo ->
                if (arquivo != null) {
                    // Navega para o visualizador de relatório com o caminho do arquivo
                    findNavController().navigate(
                        DetalheCategoriaFragmentDirections
                            .actionDetalheCategoriaFragmentToVisualizarRelatorioFragment(arquivo.absolutePath)
                    )
                    viewModel.limparEstadoOperacao()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}