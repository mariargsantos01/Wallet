package com.example.wallet.repository

import com.example.wallet.data.mock.MockData
import com.example.wallet.model.PurchaseModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePurchaseRepository : PurchaseRepository {

    private val _items = MutableStateFlow(MockData.purchases)

    override fun observePurchases(): Flow<List<PurchaseModel>> = _items.asStateFlow()

    override fun observePurchasesByCard(cardId: Long): Flow<List<PurchaseModel>> = _items.asStateFlow()

    override suspend fun getPurchases(): List<PurchaseModel> {
        delay(400)
        return _items.value
    }

    override suspend fun addPurchase(purchase: PurchaseModel, cardId: Long?) {
        _items.value = _items.value + purchase
    }

    override suspend fun deletePurchase(id: Long) {
        _items.value = _items.value.filterNot { it.id == id }
    }

    override suspend fun clearHistory() {
        _items.value = emptyList()
    }

    override suspend fun getTotalByCard(cardId: Long): Double {
        return _items.value.sumOf { it.amount }
    }

    override suspend fun countByCard(cardId: Long): Int {
        return _items.value.size
    }
}
