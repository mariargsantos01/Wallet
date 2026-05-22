package com.example.wallet.data.local.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wallet.model.BankAccount

/**
 * Catálogo global de bancos disponíveis (nome, cor, bandeiras suportadas).
 *
 * O **estado de conexão** (`isConnected`) é específico de cada usuário e
 * vive em [BankConnectionEntity]. Esta entidade contém apenas os metadados
 * estáticos de cada banco.
 */
@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey val name: String,
    val colorArgb: Long,
    val networksCsv: String
) {
    fun toModel(isConnected: Boolean): BankAccount = BankAccount(
        name = name,
        color = Color(colorArgb.toULong()),
        isConnected = isConnected,
        networks = if (networksCsv.isBlank()) emptyList() else networksCsv.split(",")
    )

    companion object {
        fun fromModel(model: BankAccount): BankAccountEntity = BankAccountEntity(
            name = model.name,
            colorArgb = model.color.value.toLong(),
            networksCsv = model.networks.joinToString(",")
        )
    }
}
