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
        val result = firestore.collection("inventory").get().await()
        return result.toObjects(Inventory::class.java)
    }

    fun getProductByCode(productCode: String): LiveData<Inventory> {
        val result = MutableLiveData<Inventory>()
        firestore.collection("inventory")
            .whereEqualTo("productCode", productCode)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    result.value = snapshot.documents[0].toObject(Inventory::class.java)
                }
            }
        return result
    }

    suspend fun deleteInventory(inventory: Inventory) {
        firestore.collection("inventory").document(inventory.id).delete().await()
    }

    suspend fun updateInventory(inventory: Inventory) {
        firestore.collection("inventory").document(inventory.id).set(inventory).await()
    }
}
