package com.example.autocontrolapp.presentation.ui.subtopico

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
import com.example.autocontrolapp.databinding.FragmentListaSubtopicoBinding
import com.example.autocontrolapp.presentation.adapter.SubtopicoAdapter
import com.example.autocontrolapp.presentation.ui.categoria.DetalheCategoriaFragmentDirections
import com.example.autocontrolapp.presentation.viewmodel.SubtopicoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListaSubtopicoFragment : Fragment() {

    private var _binding: FragmentListaSubtopicoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubtopicoViewModel by viewModels()
    private lateinit var subtopicoAdapter: SubtopicoAdapter
    
    private var categoriaId = 0

    companion object {
        private const val ARG_CATEGORIA_ID = "categoria_id"

        fun newInstance(categoriaId: Int): ListaSubtopicoFragment {
            val fragment = ListaSubtopicoFragment()
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
        _binding = FragmentListaSubtopicoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.carregarSubtopicosPorCategoria(categoriaId)
    }

    private fun setupRecyclerView() {
        subtopicoAdapter = SubtopicoAdapter()
        binding.recyclerSubtopicos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = subtopicoAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.subtopicos.collectLatest { subtopicos ->
                subtopicoAdapter.submitList(subtopicos)
                binding.emptyView.visibility = if (subtopicos.isEmpty()) View.VISIBLE else View.GONE
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
        binding.fabNovoSubtopico.setOnClickListener {
            findNavController().navigate(
                DetalheCategoriaFragmentDirections.actionDetalheCategoriaFragmentToNovoSubtopicoFragment(categoriaId)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}