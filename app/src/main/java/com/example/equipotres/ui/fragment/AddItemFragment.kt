package com.example.equipotres.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.equipotres.R
import com.example.equipotres.databinding.FragmentAddItemBinding
import android.text.Editable
import android.text.TextWatcher
import com.example.equipotres.model.Inventory
import com.example.equipotres.viewmodel.InventoryViewModel
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlin.toString

@AndroidEntryPoint
class AddItemFragment : Fragment() {

    private lateinit var _binding: FragmentAddItemBinding
    private val binding get() = _binding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        watchInputs()

        binding.btnSave.setOnClickListener {
            sumitClick()
        }
    }

    private fun setupToolbar() {
        binding.toolbarAdd.toolbar.apply {
            setNavigationIcon(R.drawable.arrow_letf)
            setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
            title = "Agregar producto"
            inflateMenu(R.menu.custom_menu)
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

    private fun watchInputs() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val productCode = binding.etProductCode.text.toString().trim()
                val productName = binding.etProductName.text.toString().trim()
                val price = binding.etPrice.text.toString().trim()
                val quantity = binding.etQuantity.text.toString().trim()

                binding.btnSave.isEnabled = productCode.isNotEmpty() &&
                        productName.isNotEmpty() &&
                        price.isNotEmpty() &&
                        quantity.isNotEmpty()
            }
        }

        binding.etProductCode.addTextChangedListener(textWatcher)
        binding.etProductName.addTextChangedListener(textWatcher)
        binding.etPrice.addTextChangedListener(textWatcher)
        binding.etQuantity.addTextChangedListener(textWatcher)

        binding.btnSave.isEnabled = false
    }

    private fun sumitClick() {
        val productCode = binding.etProductCode.text.toString()
        val name = binding.etProductName.text.toString()
        val price = binding.etPrice.text.toString()
        val quantity = binding.etQuantity.text.toString()

        val inventory = Inventory(
            productCode = productCode,
            name = name,
            price = price.toInt(),
            quantity = quantity.toInt()
        )

        inventoryViewModel.saveInventory(inventory)
        Log.d("test", inventory.toString())
        Toast.makeText(context, "Artículo guardado en Firestore!!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }
}
