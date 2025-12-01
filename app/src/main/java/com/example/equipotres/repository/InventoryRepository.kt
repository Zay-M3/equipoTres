package com.example.equipotres.repository

import com.example.equipotres.model.Inventory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // Guarda un nuevo producto en la colección "inventory" de Firestore.
    suspend fun saveInventory(inventory: Inventory) {
        firestore.collection("inventory").add(inventory).await()
    }

    // Obtiene la lista de productos desde Firestore.
    suspend fun getListInventory(): MutableList<Inventory> {
        val result = firestore.collection("inventory").get().await()
        return result.toObjects(Inventory::class.java)
    }

    // Elimina un producto de Firestore (requiere el ID del documento).
    suspend fun deleteInventory(documentId: String) {
        firestore.collection("inventory").document(documentId).delete().await()
    }

    // Actualiza un producto en Firestore (requiere el ID del documento).
    suspend fun updateInventory(documentId: String, inventory: Inventory) {
        firestore.collection("inventory").document(documentId).set(inventory).await()
    }
}
