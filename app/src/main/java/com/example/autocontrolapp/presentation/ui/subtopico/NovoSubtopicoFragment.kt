package com.example.autocontrolapp.presentation.ui.subtopico

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autocontrolapp.R
import com.example.autocontrolapp.databinding.FragmentNovoSubtopicoBinding
import com.example.autocontrolapp.domain.model.SubtopicoModel
import com.example.autocontrolapp.domain.model.TipoDadoEnum
import com.example.autocontrolapp.presentation.adapter.ValorOpcaoAdapter
import com.example.autocontrolapp.presentation.viewmodel.SubtopicoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NovoSubtopicoFragment : Fragment() {

    private var _binding: FragmentNovoSubtopicoBinding? = null
    private val binding get() = _binding!!

    private val args: NovoSubtopicoFragmentArgs by navArgs()
    private val viewModel: SubtopicoViewModel by viewModels()
    private lateinit var valoresAdapter: ValorOpcaoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNovoSubtopicoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupSpinnerTipoDado()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.iniciarNovoSubtopico(args.categoriaId)
    }

    private fun setupSpinnerTipoDado() {
        val tipos = TipoDadoEnum.values().map { it.name.replace('_', ' ') }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tipos
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoDado.adapter = adapter
        
        binding.spinnerTipoDado.setOnItemSelectedListener { _, _, position, _ ->
            val tipo = TipoDadoEnum.values()[position]
            viewModel.novoSubtopico.value?.let {
                viewModel.atualizarNovoSubtopico(it.copy(tipoDado = tipo))
            }
            atualizarVisibilidadeValoresOpcao(tipo)
        }
    }

    private fun atualizarVisibilidadeValoresOpcao(tipo: TipoDadoEnum) {
        binding.containerValoresOpcao.visibility = 
            if (tipo == TipoDadoEnum.SELECAO) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerView() {
        valoresAdapter = ValorOpcaoAdapter(
            onRemoveClick = { position -> viewModel.removerValorOpcao(position) }
        )
        
        binding.recyclerValoresOpcao.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = valoresAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.novoSubtopico.collectLatest { subtopico ->
                subtopico?.let {
                    binding.editNome.setText(it.nome)
                    binding.editDescricao.setText(it.descricao)
                    binding.switchObrigatorio.isChecked = it.obrigatorio
                    
                    val posicao = TipoDadoEnum.values().indexOf(it.tipoDado)
                    if (posicao >= 0) {
                        binding.spinnerTipoDado.setSelection(posicao)
                        atualizarVisibilidadeValoresOpcao(it.tipoDado)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.valoresOpcoes.collectLatest { valores ->
                valoresAdapter.submitList(valores)
                binding.emptyViewValores.visibility = 
                    if (valores.isEmpty() && binding.containerValoresOpcao.visibility == View.VISIBLE) 
                        View.VISIBLE else View.GONE
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
                    Snackbar.make(binding.root, "Subtópico salvo com sucesso", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    viewModel.limparEstadoOperacao()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.editNome.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                atualizarSubtopicoNoViewModel()
            }
        }

        binding.editDescricao.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                atualizarSubtopicoNoViewModel()
            }
        }

        binding.switchObrigatorio.setOnCheckedChangeListener { _, _ ->
            atualizarSubtopicoNoViewModel()
        }

        binding.btnAdicionarValor.setOnClickListener {
            exibirDialogAdicionarValor()
        }

        binding.btnSalvar.setOnClickListener {
            if (validarCampos()) {
                atualizarSubtopicoNoViewModel()
                viewModel.salvarSubtopico()
            }
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validarCampos(): Boolean {
        if (binding.editNome.text.toString().trim().isEmpty()) {
            binding.editNome.error = "Nome é obrigatório"
            return false
        }
        
        val tipoDado = TipoDadoEnum.values()[binding.spinnerTipoDado.selectedItemPosition]
        if (tipoDado == TipoDadoEnum.SELECAO && viewModel.valoresOpcoes.value.isEmpty()) {
            Snackbar.make(
                binding.root, 
                "É necessário adicionar pelo menos uma opção", 
                Snackbar.LENGTH_SHORT
            ).show()
            return false
        }
        
        return true
    }

    private fun atualizarSubtopicoNoViewModel() {
        viewModel.novoSubtopico.value?.let { subtopico ->
            val nome = binding.editNome.text.toString().trim()
            val descricao = binding.editDescricao.text.toString().trim()
            val obrigatorio = binding.switchObrigatorio.isChecked
            val tipoDado = TipoDadoEnum.values()[binding.spinnerTipoDado.selectedItemPosition]
            
            val subtopico = SubtopicoModel(
                id = subtopico.id,
                categoriaId = args.categoriaId,
                nome = nome,
                descricao = descricao,
                tipoDado = tipoDado,
                obrigatorio = obrigatorio,
                ordem = subtopico.ordem,
                valoresOpcoes = subtopico.valoresOpcoes
            )
            
            viewModel.atualizarNovoSubtopico(subtopico)
        }
    }

    private fun exibirDialogAdicionarValor() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Adicionar opção")
        
        val input = EditText(requireContext())
        input.hint = "Valor da opção"
        builder.setView(input)
        
        builder.setPositiveButton("Adicionar") { _, _ ->
            val valor = input.text.toString().trim()
            if (valor.isNotEmpty()) {
                viewModel.adicionarValorOpcao(valor)
            }
        }
        
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }
        
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}