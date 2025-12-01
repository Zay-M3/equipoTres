package com.example.equipotres.model

import java.io.Serializable

// 1. Remove Room annotations (@Entity, @PrimaryKey)
// 2. Add default values for all properties for Firestore deserialization
data class Inventory(
    val productCode: String = "",
    val name: String = "",
    val price: Int = 0,
    val quantity: Int = 0
) : Serializable
