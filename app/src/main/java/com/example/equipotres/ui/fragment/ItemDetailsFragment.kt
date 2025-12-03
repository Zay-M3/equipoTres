package com.example.equipotres.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.equipotres.R
import com.example.equipotres.databinding.FragmentItemDetailsBinding
import com.example.equipotres.model.Inventory
import com.example.equipotres.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemDetailsFragment : Fragment() {

    private var _binding: FragmentItemDetailsBinding? = null
    private val binding get() = _binding!!

    private val inventoryViewModel: InventoryViewModel by viewModels()
    private var inventoryToDelete: Inventory? = null
    private var productCodeFromArgs: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // It's better to pass only the ID, but for now we get it from the object
        val receivedInventory = arguments?.getSerializable("clave") as? Inventory
        productCodeFromArgs = receivedInventory?.productCode

        if (productCodeFromArgs == null) {
            Toast.makeText(requireContext(), "Error: no se encontró el código del producto", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupToolbar()
        observeViewModel()
        setupControllers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        productCodeFromArgs?.let { code ->
            inventoryViewModel.getProductByCode(code).observe(viewLifecycleOwner) { inventory ->
                if (inventory != null) {
                    inventoryToDelete = inventory
                    updateUi(inventory)
                } else {
                    // This block will be executed if the product is deleted from Firestore.
                    if (isAdded) { // Check if fragment is still added to an activity
                        Toast.makeText(requireContext(), "Producto no disponible", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbarDetail.toolbar.apply {
            setNavigationIcon(R.drawable.arrow_letf)
            setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
            title = "Detalle del producto"
            inflateMenu(R.menu.details_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_logout -> {
                        Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun deleteInventory() {
        inventoryToDelete?.let { inventory ->
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí") { _, _ ->
                    inventoryViewModel.deleteInventory(inventory)
                    // The observer will handle navigation. A toast is shown there.
                }
                .create()
                .show()
        } ?: Toast.makeText(requireContext(), "No se puede eliminar, producto no cargado", Toast.LENGTH_SHORT).show()
    }

    private fun setupControllers() {
        binding.btnDelete.setOnClickListener {
            deleteInventory()
        }
        binding.fabEdit.setOnClickListener {
            inventoryToDelete?.let { inventory ->
                val bundle = Bundle()
                bundle.putSerializable("dataInventory", inventory)
                findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
            }
        }
    }

    private fun updateUi(inventory: Inventory) {
        binding.tvName.text = inventory.name
        binding.tvPrecio.text = "$ ${inventory.price}"
        binding.tvCantidad.text = "${inventory.quantity}"
        binding.tvTotal.text = "$ ${
            inventoryViewModel.totalProducto(
                inventory.price,
                inventory.quantity
            )
        }"
    }
}