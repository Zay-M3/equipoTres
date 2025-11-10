package com.example.equipotres.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.equipotres.R
import com.example.equipotres.databinding.FragmentItemEditBinding
import com.example.equipotres.viewmodel.InventoryViewModel
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.equipotres.model.Inventory


class ItemEditFragment : Fragment() {

    private lateinit var _binding : FragmentItemEditBinding
    private val binding get() = _binding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    private lateinit var receivedInventory : Inventory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemEditBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        dataInventory()
        controladores()
    }

    private fun setupToolbar(){
        binding.toolbarEdit.toolbar.apply {
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

    private fun controladores(){
        binding.btnEdit.setOnClickListener {
            updateInventory()
        }
    }

    private fun dataInventory(){
        val receivedBundle = arguments
        receivedInventory = receivedBundle?.getSerializable("dataInventory") as Inventory
        binding.tvAppTitle.setText("Id: ${receivedInventory.id}")
        binding.etProductName.setText(receivedInventory.name)
        binding.etProductPrice.setText(receivedInventory.price.toString())
        binding.etProductCant.setText(receivedInventory.quantity.toString())

    }

    private fun updateInventory(){
        val name = binding.etProductName.text.toString()
        val price = binding.etProductPrice.text.toString().toInt()
        val quantity = binding.etProductCant.text.toString().toInt()
        val inventory = Inventory(receivedInventory.id, name,price, quantity)
        inventoryViewModel.updateInventory(inventory)
        Toast.makeText(context,"Artículo actualizado !!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_itemEditFragment_to_home22)
    }

}