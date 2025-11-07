package com.example.equipotres.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.equipotres.R
import com.example.equipotres.databinding.FragmentHomeBinding


class Home : Fragment(R.layout.fragment_home) {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding

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
    }



    private fun setupToolbar() {
        binding.toolbarHome.toolbar.apply {
            setNavigationIcon(R.drawable.inventory)
            title = "Inventario"
            inflateMenu(R.menu.menu_logout)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_logout -> {
                        // Lógica de logout
                        Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }
        }
    }


}