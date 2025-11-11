package com.example.equipotres.ui.fragment.home
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.equipotres.R
import com.example.equipotres.databinding.FragmentHomeBinding
import com.example.equipotres.viewmodel.InventoryViewModel
import com.example.equipotres.ui.adapter.InventoryAdapter



class Home : Fragment(R.layout.fragment_home) {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurar toolbar del fragment
        setupToolbar()
        //Enviar al fragmento de AddItemFragment cuando pulsa el boton de añadir
        binding.fbagregar.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_addItemFragment)
        }
        observadorViewModel()
    }

    //Centraliza la inicialización de observadores del ViewModel.
    private fun observadorViewModel(){
        observerListInventory()
        observerProgress()
    }

    //Muestra los ítems en el RecyclerView.
    private fun observerListInventory(){
        inventoryViewModel.getListInventory()
        inventoryViewModel.listInventory.observe(viewLifecycleOwner){ listInventory ->
            //Configura el RecyclerView:
            val recycler = binding.recyclerview
            val layoutManager =LinearLayoutManager(context)
            recycler.layoutManager = layoutManager
            val adapter = InventoryAdapter(listInventory, findNavController())
            //Creacion y asignacion del adapter
            recycler.adapter = adapter
            //Refrescar los datos del adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupToolbar() {
        binding.toolbarHome.toolbar.apply {
            setNavigationIcon(R.drawable.inventory)
            title = "Inventario"
            inflateMenu(R.menu.menu_logout)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_logout -> {
                        findNavController().navigate(R.id.action_home2_to_loginFragment)

                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun observerProgress(){
        inventoryViewModel.progresState.observe(viewLifecycleOwner){status ->
            binding.progress.isVisible = status
        }
    }
}