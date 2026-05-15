package com.example.wallet.repository

import com.example.wallet.data.mock.MockData
import com.example.wallet.model.PurchaseModel
import kotlinx.coroutines.delay

class FakePurchaseRepository : PurchaseRepository {

    override suspend fun getPurchases(): List<PurchaseModel> {
        delay(400)
        return MockData.purchases
    }
}

