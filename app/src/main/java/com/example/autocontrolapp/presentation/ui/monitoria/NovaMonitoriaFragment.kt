package com.example.autocontrolapp.presentation.ui.monitoria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autocontrolapp.R
import com.example.autocontrolapp.databinding.FragmentNovaMonitoriaBinding
import com.example.autocontrolapp.domain.model.SubtopicoModel
import com.example.autocontrolapp.domain.model.TipoDadoEnum
import com.example.autocontrolapp.presentation.adapter.SubtopicoRespostaAdapter
import com.example.autocontrolapp.presentation.viewmodel.MonitoriaViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NovaMonitoriaFragment : Fragment() {

    private var _binding: FragmentNovaMonitoriaBinding? = null
    private val binding get() = _binding!!

    private val args: NovaMonitoriaFragmentArgs by navArgs()
    private val viewModel: MonitoriaViewModel by viewModels()
    private lateinit var subtopicoAdapter: SubtopicoRespostaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNovaMonitoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.carregarSubtopicosDaCategoria(args.categoriaId)
        viewModel.iniciarNovaMonitoria()
    }

    private fun setupRecyclerView() {
        subtopicoAdapter = SubtopicoRespostaAdapter(
            onRespostaChange = { subtopicoId, resposta ->
                viewModel.registrarResposta(subtopicoId, resposta)
            },
            onObservacaoChange = { subtopicoId, observacao ->
                viewModel.registrarObservacaoResposta(subtopicoId, observacao)
            }
        )

        binding.recyclerSubtopicos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = subtopicoAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.subtopicosCategoria.collectLatest { subtopicos ->
                subtopicoAdapter.submitList(subtopicos)
                binding.emptyView.visibility = if (subtopicos.isEmpty()) View.VISIBLE else View.GONE
            }
        }

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
                    Snackbar.make(binding.root, "Monitoria salva com sucesso", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    viewModel.limparEstadoOperacao()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSalvar.setOnClickListener {
            val realizadoPor = binding.edtRealizadoPor.text.toString().trim()
            val observacoes = binding.edtObservacoes.text.toString().trim()
            
            viewModel.setRealizadaPor(realizadoPor)
            viewModel.setObservacoesGerais(observacoes)
            
            if (realizadoPor.isBlank()) {
                binding.edtRealizadoPor.error = "Informe quem realizou a monitoria"
                return@setOnClickListener
            }
            
            viewModel.salvarMonitoria(args.categoriaId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}