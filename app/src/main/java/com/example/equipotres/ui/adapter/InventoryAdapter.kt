package com.example.equipotres.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.equipotres.databinding.ItemInventoyBinding
import com.example.equipotres.model.Inventory
import com.example.equipotres.ui.viewholder.InventoryViewHolder

class InventoryAdapter(private val listInventory: MutableList<Inventory>, private val navController: NavController):
RecyclerView.Adapter<InventoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InventoryViewHolder {
        val binding = ItemInventoyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(binding, navController)
    }

    override fun onBindViewHolder(
        holder: InventoryViewHolder,
        position: Int
    ) {
        val inventory = listInventory[position]
        holder.setItemInventory(inventory)

    }

    override fun getItemCount(): Int {
        return listInventory.size
    }

}