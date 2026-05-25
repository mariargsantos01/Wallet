package com.example.wallet.repository

import com.example.wallet.model.BankAccount
import kotlinx.coroutines.flow.Flow

interface BankRepository {
    /** Lista de bancos do catálogo com o status de conexão **do usuário atual**. */
    fun observeBanks(): Flow<List<BankAccount>>

    suspend fun getBanks(): List<BankAccount>

    /** Conecta um banco ao usuário logado. */
    suspend fun connect(bankName: String)

    /** Desconecta um banco do usuário logado. */
    suspend fun disconnect(bankName: String)

    /** Desconecta todos os bancos do usuário logado. */
    suspend fun disconnectAll()
}
