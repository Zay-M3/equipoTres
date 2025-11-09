package com.example.equipotres.repository
import android.content.Context
import com.example.equipotres.data.InventoryDB
import com.example.equipotres.data.InventoryDao
import com.example.equipotres.model.Inventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class InventoryRepository (val context : Context) {
    private var inventoryDao: InventoryDao = InventoryDB.getDatabase(context).inventoryDao()

    //Inserta un nuevo registro en la base de datos local.
    suspend fun saveInventory(inventory: Inventory){
        withContext(Dispatchers.IO){
            inventoryDao.saveInventory(inventory)
        }
    }

    //Metodo central que interactúa directamente con la muestra de ítems en el RecyclerView.
    suspend fun getListInventory():MutableList<Inventory>{
        return withContext(Dispatchers.IO){
            inventoryDao.getListInventory()
        }
    }

    //Elimina un registro específico de la base de datos.
    suspend fun deleteInventory(inventory: Inventory){
        withContext(Dispatchers.IO){
            inventoryDao.deleteInventory(inventory)
        }
    }

    //Actualiza los datos de un registro existente en la base de datos.
    suspend fun updateRepositoy(inventory: Inventory){
        withContext(Dispatchers.IO){
            inventoryDao.updateInventory(inventory)
        }
    }

}