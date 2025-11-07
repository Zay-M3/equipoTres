package com.example.equipotres.repository

import android.content.Context
import com.example.equipotres.data.InventoryDB
import com.example.equipotres.data.InventoryDao
import com.example.equipotres.model.Inventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class InventoryRepository (val context : Context) {
    private var inventoryDao: InventoryDao = InventoryDB.getDatabase(context).inventoryDao()

    suspend fun saveInventory(inventory: Inventory){
        withContext(Dispatchers.IO){
            inventoryDao.saveInventory(inventory)
        }
    }

}