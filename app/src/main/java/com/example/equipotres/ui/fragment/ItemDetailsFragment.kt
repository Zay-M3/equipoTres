package com.example.equipotres.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.equipotres.R
import com.example.equipotres.databinding.FragmentItemDetailsBinding
import com.example.equipotres.model.Inventory
import androidx.navigation.fragment.findNavController
import com.example.equipotres.viewmodel.InventoryViewModel
import androidx.fragment.app.viewModels


class ItemDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentItemDetailsBinding
    private val binding get() = _binding
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var receivedInventory: Inventory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentItemDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar toolbar del fragment
        setupToolbar()
        dataInventory()
        controladores()
    }

    private fun setupToolbar(){
        binding.toolbarDetail.apply {
            setNavigationIcon(R.drawable.arrow_letf)
            setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
            title = "Detalle del producto"
            inflateMenu(R.menu.custom_menu)
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

    private fun controladores() {

        binding.fabEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("dataInventory", receivedInventory)
            findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
        }
    }


    private fun dataInventory() {
        val receivedBundle = arguments
        receivedInventory = receivedBundle?.getSerializable("clave") as Inventory
        binding.tvName.text = "${receivedInventory.name}"
        binding.tvPrecio.text = "$ ${receivedInventory.price}"
        binding.tvCantidad.text = "${receivedInventory.quantity}"
        binding.tvTotal.text = "$ ${
            inventoryViewModel.totalProducto(
                receivedInventory.price,
                receivedInventory.quantity
            )
        }"
    }

}