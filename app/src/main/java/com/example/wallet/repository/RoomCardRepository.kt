package com.example.wallet.repository

import com.example.wallet.data.local.dao.CardDao
import com.example.wallet.data.local.entity.CardEntity
import com.example.wallet.model.CardModel
import com.example.wallet.utils.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Implementação real (Room/SQLite) do [CardRepository].
 *
 * Os cartões são isolados por `accountId` — o filtro é reativo ao
 * usuário logado em [SessionManager], então trocar de usuário troca
 * automaticamente a lista de cartões observada.
 */
class RoomCardRepository(
    private val cardDao: CardDao,
    private val sessionManager: SessionManager
) : CardRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCards(): Flow<List<CardModel>> =
        sessionManager.currentUserId.flatMapLatest { accountId ->
            if (accountId == null) flowOf(emptyList())
            else cardDao.observeByAccount(accountId).map { list -> list.map { it.toModel() } }
        }

    private fun requireAccountId(): Long =
        sessionManager.getCurrentUserId()
            ?: error("Nenhum usuário logado — não é possível operar sobre cartões.")

    override suspend fun getCards(): List<CardModel> {
        val accountId = sessionManager.getCurrentUserId() ?: return emptyList()
        return cardDao.getByAccount(accountId).map { it.toModel() }
    }

    override suspend fun getCardById(id: Long): CardModel? =
        cardDao.getById(id)?.toModel()

    override suspend fun addCard(card: CardModel): Long {
        val accountId = requireAccountId()
        return cardDao.insert(CardEntity.fromModel(card, accountId))
    }

    override suspend fun updateCard(card: CardModel) {
        val existing = cardDao.getById(card.id) ?: return
        cardDao.update(CardEntity.fromModel(card, existing.accountId))
    }

    override suspend fun deleteCard(id: Long) {
        cardDao.deleteById(id)
    }

    override suspend fun setFavorite(id: Long, isFavorite: Boolean) {
        cardDao.setFavorite(id, isFavorite)
    }

    override suspend fun setActive(id: Long, isActive: Boolean) {
        cardDao.setActive(id, isActive)
    }

    override suspend fun updateLimits(id: Long, dayLimit: Double, nightLimit: Double) {
        cardDao.updateLimits(id, dayLimit, nightLimit)
    }

    override suspend fun hasCards(): Boolean {
        val accountId = sessionManager.getCurrentUserId() ?: return false
        return cardDao.getByAccount(accountId).isNotEmpty()
    }
}
