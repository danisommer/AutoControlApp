package com.example.autocontrolapp.presentation.ui.categoria

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
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.autocontrolapp.R
import com.example.autocontrolapp.databinding.FragmentDetalheCategoriaBinding
import com.example.autocontrolapp.domain.model.CategoriaModel
import com.example.autocontrolapp.presentation.viewmodel.CategoriaViewModel
import com.example.autocontrolapp.presentation.ui.subtopico.ListaSubtopicoFragment
import com.example.autocontrolapp.presentation.ui.monitoria.ListaMonitoriaFragment
import com.example.autocontrolapp.presentation.ui.relatorio.ListaRelatorioFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class DetalheCategoriaFragment : Fragment() {

    private var _binding: FragmentDetalheCategoriaBinding? = null
    private val binding get() = _binding!!
    
    private val args: DetalheCategoriaFragmentArgs by navArgs()
    private val viewModel: CategoriaViewModel by viewModels()
    
    private var categoriaAtual: CategoriaModel? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalheCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupMenu()
        
        viewModel.carregarCategorias(false)
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categorias
                .collectLatest { categorias ->
                    val categoria = categorias.find { it.id == args.categoriaId }
                    if (categoria != null) {
                        categoriaAtual = categoria
                        exibirDadosCategoria(categoria)
                        setupViewPager(categoria)
                    } else {
                        Snackbar.make(binding.root, "Categoria não encontrada", Snackbar.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
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
    
    private fun exibirDadosCategoria(categoria: CategoriaModel) {
        binding.tvNomeCategoria.text = categoria.nome
        binding.tvDescricaoCategoria.text = categoria.descricao.ifEmpty { "Sem descrição" }
        binding.tvPeriodoCategoria.text = "A cada ${categoria.periodo} dias"
        
        val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        
        binding.tvUltimaVerificacao.text = categoria.ultimaVerificacao?.let { formatoData.format(it) } ?: "Não verificado"
        binding.tvProximaVerificacao.text = categoria.proximaVerificacao?.let { formatoData.format(it) } ?: "Não agendado"
        
        binding.switchStatus.isChecked = categoria.ativa
        binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
            categoriaAtual?.let {
                val categoriaAtualizada = it.copy(ativa = isChecked)
                viewModel.atualizarCategoria(categoriaAtualizada)
            }
        }
    }
    
    private fun setupViewPager(categoria: CategoriaModel) {
        val adapter = DetalheCategoriaAdapter(this, categoria.id)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Subtópicos"
                1 -> "Monitorias"
                2 -> "Relatórios"
                else -> "Tab ${position + 1}"
            }
        }.attach()
    }
    
    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_detalhe_categoria, menu)
            }
            
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit_categoria -> {
                        categoriaAtual?.let {
                            findNavController().navigate(
                                DetalheCategoriaFragmentDirections.actionDetalheCategoriaFragmentToEditarCategoriaFragment(it.id)
                            )
                        }
                        true
                    }
                    R.id.action_nova_monitoria -> {
                        categoriaAtual?.let {
                            findNavController().navigate(
                                DetalheCategoriaFragmentDirections.actionDetalheCategoriaFragmentToNovaMonitoriaFragment(it.id)
                            )
                        }
                        true
                    }
                    R.id.action_novo_subtopico -> {
                        categoriaAtual?.let {
                            findNavController().navigate(
                                DetalheCategoriaFragmentDirections.actionDetalheCategoriaFragmentToNovoSubtopicoFragment(it.id)
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private inner class DetalheCategoriaAdapter(
        fragment: Fragment,
        private val categoriaId: Int
    ) : FragmentStateAdapter(fragment) {
        
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> ListaSubtopicoFragment.newInstance(categoriaId)
                1 -> ListaMonitoriaFragment.newInstance(categoriaId)
                2 -> ListaRelatorioFragment.newInstance(categoriaId)
                else -> throw IllegalArgumentException("Posição inválida: $position")
            }
        }
    }
}