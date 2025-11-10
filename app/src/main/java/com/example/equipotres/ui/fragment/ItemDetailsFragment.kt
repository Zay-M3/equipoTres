package com.example.equipotres.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.equipotres.R
import com.example.equipotres.databinding.FragmentItemDetailsBinding

class ItemDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentItemDetailsBinding
    private val binding get() = _binding



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
}