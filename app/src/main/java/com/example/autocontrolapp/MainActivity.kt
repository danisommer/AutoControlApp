package com.example.autocontrolapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.autocontrolapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        // Configuração do Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        // Define os destinos de nível superior (que mostram o drawer)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_categorias
            ),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Configura o FAB para adicionar nova categoria quando estiver na tela de categorias
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_categorias -> {
                    binding.appBarMain.fab?.apply {
                        isVisible = true
                        setOnClickListener {
                            navController.navigate(R.id.action_nav_categorias_to_novaCategoriaFragment)
                        }
                    }
                }
                R.id.detalheMonitoriaFragment -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    binding.appBarMain.fab?.isVisible = false
                }
                R.id.detalheCategoriaFragment -> {
                    binding.appBarMain.fab?.apply {
                        isVisible = true
                        setOnClickListener {
                            // Obtém o ID da categoria atual do fragmento de destino
                            val currentDestination = navController.currentBackStackEntry?.destination
                            val categoriaId = currentDestination?.arguments?.getInt("categoriaId") ?: 0
                            
                            val action = DetalheCategoriaFragmentDirections
                                .actionDetalheCategoriaFragmentToNovaMonitoriaFragment(categoriaId)
                            navController.navigate(action)
                        }
                    }
                }
                else -> {
                    binding.appBarMain.fab?.isVisible = false
                }
            }
        }

        // Verifica se foi aberto a partir de uma notificação
        intent.extras?.let { extras ->
            if (extras.containsKey("categoria_id")) {
                val categoriaId = extras.getInt("categoria_id")
                val action = HomeFragmentDirections.actionNavHomeToDetalheCategoriaFragment(categoriaId)
                navController.navigate(action)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}