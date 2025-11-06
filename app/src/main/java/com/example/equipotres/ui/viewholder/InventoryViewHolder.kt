package com.example.equipotres.ui.viewholder

import android.os.Bundle
import androidx.navigation.NavController
import com.example.equipotres.R
import androidx.recyclerview.widget.RecyclerView
import com.example.equipotres.databinding.ItemInventoyBinding
import com.example.equipotres.model.Inventory
class InventoryViewHolder(binding: ItemInventoyBinding, navController: NavController):
    RecyclerView.ViewHolder(binding.root) {
    val bindingItem = binding
    val navController = navController
    fun setItemInventory(inventory: Inventory) {
        bindingItem.tvName.text = inventory.name
        bindingItem.tvPrice.text = "$ ${inventory.price}"
        bindingItem.tvQuantity.text = "${inventory.quantity}"

        bindingItem.cvInventory.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("clave", inventory)
            navController.navigate(R.id.action_home2_to_itemDetailsFragment, bundle)
        }
    }
}