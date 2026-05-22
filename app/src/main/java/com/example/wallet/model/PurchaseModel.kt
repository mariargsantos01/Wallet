package com.example.wallet.model

data class PurchaseModel(
    val id: Long = 0L,
    val title: String,
    val amount: Double,
    val date: String
)
