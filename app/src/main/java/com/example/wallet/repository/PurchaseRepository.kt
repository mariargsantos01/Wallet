package com.example.wallet.repository

import com.example.wallet.model.PurchaseModel

interface PurchaseRepository {
    suspend fun getPurchases(): List<PurchaseModel>
}

