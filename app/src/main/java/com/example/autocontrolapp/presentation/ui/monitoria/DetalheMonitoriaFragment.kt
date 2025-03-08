package com.example.autocontrolapp.presentation.ui.monitoria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autocontrolapp.R
import com.example.autocontrolapp.databinding.FragmentDetalheMonitoriaBinding
import com.example.autocontrolapp.domain.model.MonitoriaModel
import com.example.autocontrolapp.presentation.adapter.RespostaMonitoriaAdapter
import com.example.autocontrolapp.presentation.viewmodel.MonitoriaViewModel
import com.example.autocontrolapp.presentation.viewmodel.RelatorioViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class DetalheMonitoriaFragment : Fragment() {

    private var _binding: FragmentDetalheMonitoriaBinding? = null
    private val binding get() = _binding!!
    
    private val args: DetalheMonitoriaFragmentArgs by navArgs()
    private val monitoriaViewModel: MonitoriaViewModel by viewModels()
    private val relatorioViewModel: RelatorioViewModel by viewModels()
    
    private lateinit var respostasAdapter: RespostaMonitoriaAdapter
    private var monitoriaAtual: MonitoriaModel? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalheMonitoriaBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupMenu()
        
        // Carrega os detalhes da monitoria
        lifecycleScope.launch {
            val monitoria = monitoriaViewModel.obterMonitoriaPorId(args.monitoriaId)
            if (monitoria != null) {
                monitoriaAtual = monitoria
                atualizarUI(monitoria)
            } else {
                Snackbar.make(binding.root, "Monitoria não encontrada", Snackbar.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }
    }
    
    private fun setupRecyclerView() {
        respostasAdapter = RespostaMonitoriaAdapter()
        binding.recyclerRespostas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = respostasAdapter
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            relatorioViewModel.carregando.collectLatest { carregando ->
                binding.progressBar.visibility = if (carregando) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            relatorioViewModel.erro.collectLatest { erro ->
                if (erro != null) {
                    Snackbar.make(binding.root, erro, Snackbar.LENGTH_LONG).show()
                    relatorioViewModel.limparEstadoOperacao()
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            relatorioViewModel.arquivoRelatorio.collectLatest { arquivo ->
                if (arquivo != null) {
                    // Navega para o visualizador de relatório
                    findNavController().navigate(
                        DetalheMonitoriaFragmentDirections
                            .actionDetalheMonitoriaFragmentToVisualizarRelatorioFragment(arquivo.absolutePath)
                    )
                    relatorioViewModel.limparEstadoOperacao()
                }
            }
        }
    }
    
    private fun atualizarUI(monitoria: MonitoriaModel) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        
        binding.tvDataRealizacao.text = dateFormat.format(monitoria.dataRealizacao)
        binding.tvRealizadoPor.text = monitoria.realizadaPor
        binding.tvObservacoes.text = monitoria.observacoes.ifEmpty { "Sem observações" }
        
        respostasAdapter.submitList(monitoria.respostas)
    }
    
    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_detalhe_monitoria, menu)
            }
            
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_gerar_relatorio -> {
                        gerarRelatorio()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    
    private fun gerarRelatorio() {
        val monitoriaId = args.monitoriaId
        relatorioViewModel.gerarRelatorio(requireContext(), monitoriaId)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}