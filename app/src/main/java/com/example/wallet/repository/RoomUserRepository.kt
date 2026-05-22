package com.example.wallet.repository

import com.example.wallet.data.local.dao.AccountDao
import com.example.wallet.data.local.entity.AccountEntity
import com.example.wallet.model.UserModel
import com.example.wallet.utils.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

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
    private val sessionManager: SessionManager
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
        val account = existing ?: AccountEntity(
            id = UUID.randomUUID().toString(),
            name = normalized.substringBefore("@").replaceFirstChar { it.uppercase() },
            email = normalized
        ).also { accountDao.insert(it) }

        sessionManager.setCurrentUser(account.id)
        return account.toUserModel()
    }

    override suspend fun signUp(name: String, email: String, password: String): UserModel {
        val normalized = email.trim().lowercase()
        val existing = accountDao.getByEmail(normalized)
        val account = existing ?: AccountEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            email = normalized
        ).also { accountDao.insert(it) }

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
}

