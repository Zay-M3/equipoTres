package com.example.equipotres.model

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Inventory(
    @DocumentId
    val id: String = "",
    val productCode: String = "",
    val name: String = "",
    val price: Int = 0,
    val quantity: Int = 0
) : Serializable
