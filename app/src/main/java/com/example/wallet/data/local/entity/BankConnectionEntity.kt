package com.example.wallet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Conexão entre um usuário (account) e um banco do catálogo.
 *
 * A simples existência de uma linha indica que aquele usuário tem aquele
 * banco conectado. PK composta (accountId, bankName) garante unicidade.
 * `accountId` é FK com CASCADE para que, ao excluir a conta, as conexões
 * sejam removidas automaticamente.
 */
@Entity(
    tableName = "bank_connections",
    primaryKeys = ["accountId", "bankName"],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BankAccountEntity::class,
            parentColumns = ["name"],
            childColumns = ["bankName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bankName"), Index("accountId")]
)
data class BankConnectionEntity(
    val accountId: String,
    val bankName: String
)

