package com.example.wallet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Conta de usuário (proprietário das cartões/transações).
 *
 * Representa a "conta" criada no app — equivalente ao [UserModel] de domínio,
 * porém com campos adicionais úteis para persistência (saldo, timestamps).
 */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val balance: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

