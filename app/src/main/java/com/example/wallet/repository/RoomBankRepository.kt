package com.example.wallet.repository

import com.example.wallet.data.local.dao.BankAccountDao
import com.example.wallet.data.local.dao.BankConnectionDao
import com.example.wallet.data.local.entity.BankAccountEntity
import com.example.wallet.data.local.entity.BankConnectionEntity
import com.example.wallet.model.BankAccount
import com.example.wallet.utils.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Implementação real do [BankRepository] usando Room.
 *
 * O catálogo de bancos é global (mesmas opções para todo mundo),
 * mas o **status de conexão é por usuário** — vive em
 * [BankConnectionEntity] e é filtrado pelo id da sessão atual.
 */
class RoomBankRepository(
    private val bankDao: BankAccountDao,
    private val connectionDao: BankConnectionDao,
    private val sessionManager: SessionManager
) : BankRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeBanks(): Flow<List<BankAccount>> =
        sessionManager.currentUserId.flatMapLatest { accountId ->
            if (accountId == null) {
                // Sem sessão: catálogo todo desconectado.
                bankDao.observeAll().map { list -> list.map { it.toModel(false) } }
            } else {
                combine(
                    bankDao.observeAll(),
                    connectionDao.observeConnectedBankNames(accountId)
                ) { banks, connectedNames ->
                    val connectedSet = connectedNames.toSet()
                    banks.map { it.toModel(it.name in connectedSet) }
                }
            }
        }

    override suspend fun getBanks(): List<BankAccount> {
        val accountId = sessionManager.getCurrentUserId()
        val connectedSet = accountId?.let { connectionDao.getConnectedBankNames(it).toSet() } ?: emptySet()
        return bankDao.getAll().map { it.toModel(it.name in connectedSet) }
    }

    override suspend fun connect(bankName: String) {
        val accountId = sessionManager.getCurrentUserId() ?: return
        connectionDao.connect(BankConnectionEntity(accountId = accountId, bankName = bankName))
    }

    override suspend fun disconnect(bankName: String) {
        val accountId = sessionManager.getCurrentUserId() ?: return
        connectionDao.disconnect(accountId, bankName)
    }

    override suspend fun disconnectAll() {
        val accountId = sessionManager.getCurrentUserId() ?: return
        connectionDao.disconnectAll(accountId)
    }

    suspend fun seedIfEmpty(initial: List<BankAccount>) {
        if (bankDao.count() == 0) {
            bankDao.insertAll(initial.map { BankAccountEntity.fromModel(it) })
        }
    }
}
