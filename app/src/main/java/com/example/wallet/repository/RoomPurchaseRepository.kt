package com.example.wallet.repository

import com.example.wallet.data.local.dao.PurchaseDao
import com.example.wallet.data.local.entity.PurchaseEntity
import com.example.wallet.model.PurchaseModel
import com.example.wallet.utils.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Implementação real do [PurchaseRepository] usando Room.
 *
 * As compras são isoladas por `accountId` — o filtro é reativo ao
 * usuário logado em [SessionManager].
 */
class RoomPurchaseRepository(
    private val purchaseDao: PurchaseDao,
    private val sessionManager: SessionManager
) : PurchaseRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePurchases(): Flow<List<PurchaseModel>> =
        sessionManager.currentUserId.flatMapLatest { accountId ->
            if (accountId == null) flowOf(emptyList())
            else purchaseDao.observeByAccount(accountId).map { list -> list.map { it.toModel() } }
        }

    override fun observePurchasesByCard(cardId: String): Flow<List<PurchaseModel>> =
        purchaseDao.observeByCard(cardId).map { list -> list.map { it.toModel() } }

    override suspend fun getPurchases(): List<PurchaseModel> {
        val accountId = sessionManager.getCurrentUserId() ?: return emptyList()
        return purchaseDao.getByAccount(accountId).map { it.toModel() }
    }

    override suspend fun addPurchase(purchase: PurchaseModel, cardId: String?) {
        val accountId = sessionManager.getCurrentUserId() ?: return
        purchaseDao.insert(
            PurchaseEntity(
                id = purchase.id,
                accountId = accountId,
                cardId = cardId,
                title = purchase.title,
                amount = purchase.amount,
                date = purchase.date
            )
        )
    }

    override suspend fun deletePurchase(id: String) {
        purchaseDao.deleteById(id)
    }

    override suspend fun clearHistory() {
        val accountId = sessionManager.getCurrentUserId() ?: return
        purchaseDao.clearByAccount(accountId)
    }
}
