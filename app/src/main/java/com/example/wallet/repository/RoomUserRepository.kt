package com.example.wallet.repository

import com.example.wallet.data.local.dao.AccountDao
import com.example.wallet.data.local.dao.CardDao
import com.example.wallet.data.local.dao.PurchaseDao
import com.example.wallet.data.local.entity.AccountEntity
import com.example.wallet.data.local.entity.CardEntity
import com.example.wallet.data.local.entity.PurchaseEntity
import com.example.wallet.model.UserModel
import com.example.wallet.utils.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Implementação real de [UserRepository] usando Room + [SessionManager].
 *
 * Cada conta (usuário) é uma linha na tabela `accounts` e tem seus próprios
 * cartões/compras isolados por `accountId` (FK com CASCADE).
 *
 * - [login]: busca a conta pelo e-mail; se não existir, cria uma nova.
 *   Em ambos os casos marca a conta como sessão atual via [SessionManager].
 * - [signUp]: cria uma nova conta (ou reutiliza pelo e-mail) e marca como sessão.
 * - [logout]: limpa apenas o id da sessão — os dados persistem no banco
 *   para serem recuperados em um próximo login com o mesmo e-mail.
 */
class RoomUserRepository(
    private val accountDao: AccountDao,
    private val sessionManager: SessionManager,
    private val cardDao: CardDao? = null,
    private val purchaseDao: PurchaseDao? = null
) : UserRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCurrentUser(): Flow<UserModel?> =
        sessionManager.currentUserId.flatMapLatest { id ->
            if (id == null) flowOf(null)
            else accountDao.observeById(id).map { it?.toUserModel() }
        }

    override suspend fun login(email: String, password: String): UserModel {
        val normalized = email.trim().lowercase()
        val existing = accountDao.getByEmail(normalized)
        val isNewAccount = existing == null
        val account = if (existing != null) {
            existing
        } else {
            val entity = AccountEntity(
                name = normalized.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = normalized
            )
            val newId = accountDao.insert(entity)
            entity.copy(id = newId)
        }

        sessionManager.setCurrentUser(account.id)

        // Seed de dados mockados para o usuário "teste123"
        if (isNewAccount && normalized == "teste123") {
            seedTestData(account.id)
        }

        return account.toUserModel()
    }

    override suspend fun signUp(name: String, email: String, password: String): UserModel {
        val normalized = email.trim().lowercase()
        val existing = accountDao.getByEmail(normalized)
        val account = if (existing != null) {
            existing
        } else {
            val entity = AccountEntity(
                name = name,
                email = normalized
            )
            val newId = accountDao.insert(entity)
            entity.copy(id = newId)
        }

        sessionManager.setCurrentUser(account.id)
        return account.toUserModel()
    }

    override suspend fun getCurrentUser(): UserModel? {
        val id = sessionManager.getCurrentUserId() ?: return null
        return accountDao.getById(id)?.toUserModel()
    }

    override suspend fun logout() {
        // Mantém os dados persistidos; apenas encerra a sessão atual.
        sessionManager.clear()
    }

    override suspend fun deleteAccount() {
        val id = sessionManager.getCurrentUserId() ?: return
        accountDao.deleteById(id) // FK CASCADE remove cartões e compras
        sessionManager.clear()
    }

    private fun AccountEntity.toUserModel() = UserModel(
        id = id,
        name = name,
        email = email
    )

    private suspend fun seedTestData(accountId: Long) {
        val cDao = cardDao ?: return
        val pDao = purchaseDao ?: return

        // Cria um cartão de teste
        val cardId = cDao.insert(
            CardEntity(
                accountId = accountId,
                name = "Wallet Black",
                lastDigits = "7842",
                limit = 10000.0,
                isFavorite = true,
                isActive = true,
                dayLimit = 5000.0,
                nightLimit = 2000.0,
                brand = "Visa",
                expiry = "06/29"
            )
        )

        // Compras mockadas associadas ao cartão
        val purchases = listOf(
            PurchaseEntity(accountId = accountId, cardId = cardId, title = "Supermercado Extra", amount = 287.45, date = "20/05/2026"),
            PurchaseEntity(accountId = accountId, cardId = cardId, title = "Netflix", amount = 55.90, date = "18/05/2026"),
            PurchaseEntity(accountId = accountId, cardId = cardId, title = "Posto Shell", amount = 220.00, date = "15/05/2026"),
            PurchaseEntity(accountId = accountId, cardId = cardId, title = "iFood", amount = 67.80, date = "14/05/2026"),
            PurchaseEntity(accountId = accountId, cardId = cardId, title = "Amazon", amount = 349.90, date = "12/05/2026"),
            PurchaseEntity(accountId = accountId, cardId = cardId, title = "Farmácia Drogasil", amount = 98.50, date = "10/05/2026"),
            PurchaseEntity(accountId = accountId, cardId = cardId, title = "Uber", amount = 32.70, date = "08/05/2026"),
            PurchaseEntity(accountId = accountId, cardId = cardId, title = "Padaria Real", amount = 18.90, date = "07/05/2026")
        )
        pDao.insertAll(purchases)
    }
}
