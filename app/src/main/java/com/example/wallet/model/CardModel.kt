package com.example.wallet.model

data class CardModel(
    val id: String,
    val name: String,
    val lastDigits: String,
    val limit: Double,
    val isFavorite: Boolean = false,
    val isActive: Boolean = true,
    val dayLimit: Double = 5000.0,
    val nightLimit: Double = 2000.0,
    val brand: String = "Visa",
    val expiry: String = "12/28"
)

