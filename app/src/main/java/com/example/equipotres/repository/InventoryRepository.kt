package com.example.equipotres.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.equipotres.model.Inventory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun saveInventory(inventory: Inventory) {
        firestore.collection("inventory").add(inventory).await()
    }

    suspend fun getListInventory(): MutableList<Inventory> {
        val snapshot = firestore.collection("inventory").get().await()
        val inventoryList = mutableListOf<Inventory>()
        // Firestore's toObjects function doesn't populate the document ID.
        // We need to iterate and set it manually.
        for (document in snapshot.documents) {
            val inventory = document.toObject(Inventory::class.java)
            if (inventory != null) {
                inventory.id = document.id
                inventoryList.add(inventory)
            }
        }
        return inventoryList
    }

    fun getProductByCode(productCode: String): LiveData<Inventory> {
        val result = MutableLiveData<Inventory?>()
        firestore.collection("inventory")
            .whereEqualTo("productCode", productCode)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle the error, maybe log it or post a specific state
                    result.value = null
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    val inventory = document.toObject(Inventory::class.java)
                    // Manually set the document ID on the object
                    inventory?.id = document.id
                    result.value = inventory
                } else {
                    result.value = null
                }
            }
        return result as LiveData<Inventory>
    }

    suspend fun deleteInventory(inventory: Inventory) {
        // This requires inventory.id to be correctly set.
        firestore.collection("inventory").document(inventory.id).delete().await()
    }

    suspend fun updateInventory(inventory: Inventory) {
        // This requires inventory.id to be correctly set.
        firestore.collection("inventory").document(inventory.id).set(inventory).await()
    }
}
