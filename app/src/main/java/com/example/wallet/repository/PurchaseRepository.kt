package com.example.wallet.repository

import com.example.wallet.model.PurchaseModel
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {
    /** Compras de toda a conta corrente (uso legado/agregação). */
    fun observePurchases(): Flow<List<PurchaseModel>>

    /** Compras de um cartão específico. Um cartão novo retorna lista vazia. */
    fun observePurchasesByCard(cardId: Long): Flow<List<PurchaseModel>>

    suspend fun getPurchases(): List<PurchaseModel>
    suspend fun addPurchase(purchase: PurchaseModel, cardId: Long? = null)
    suspend fun deletePurchase(id: Long)
    suspend fun clearHistory()

    /** Soma total de compras do cartão. */
    suspend fun getTotalByCard(cardId: Long): Double

    /** Quantidade de compras do cartão. */
    suspend fun countByCard(cardId: Long): Int
}
