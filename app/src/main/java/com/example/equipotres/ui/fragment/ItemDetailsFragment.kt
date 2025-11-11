package com.example.equipotres.ui.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.equipotres.R
import com.example.equipotres.databinding.FragmentItemDetailsBinding
import com.example.equipotres.model.Inventory
import androidx.navigation.fragment.findNavController
import com.example.equipotres.viewmodel.InventoryViewModel
import androidx.fragment.app.viewModels


class ItemDetailsFragment : Fragment() {
    private lateinit var _binding: FragmentItemDetailsBinding
    private val binding get() = _binding
    //Instancia del ViewModel para manejar la lógica del inventario
    private val inventoryViewModel: InventoryViewModel by viewModels()

    private lateinit var receivedInventory: Inventory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ✅ Recibir el objeto Inventory enviado desde el ViewHolder
        val args = arguments
        receivedInventory = args?.getSerializable("clave") as? Inventory
            ?: run {
                Toast.makeText(requireContext(), "Error: no se encontró el producto", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return
            }
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


    private fun deleteInventory() {
        // Crear diálogo de confirmación
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estas seguro de que deseas eliminar este producto?")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Cierra el diálogo
            }
            .setPositiveButton("Sí") { dialog, _ ->
                inventoryViewModel.deleteInventory(receivedInventory)
                Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .create()
            .show()
    }

    private fun controladores() {
        binding.btnDelete.setOnClickListener {
            deleteInventory()
        }
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