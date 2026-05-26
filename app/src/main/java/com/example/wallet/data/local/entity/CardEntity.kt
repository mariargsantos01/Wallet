package com.example.wallet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.wallet.model.CardModel

/**
 * Entidade Room de um cartão.
 *
 * Inclui todos os campos do domínio + configurações de gerenciamento
 * (favorito, ativo, limites diurno/noturno). FK em [accountId] com
 * `onDelete = CASCADE` para que excluir a conta apague também os cartões.
 */
@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["accountId"])]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val accountId: Long,
    val name: String,
    val lastDigits: String,
    val limit: Double,
    val isFavorite: Boolean = false,
    val isActive: Boolean = true,
    val dayLimit: Double = 5000.0,
    val nightLimit: Double = 2000.0,
    val brand: String = "Visa",
    val expiry: String = "12/28",
    val bankColor: Long = 0xFF171717,
    val bankName: String = "",
    val isTemporary: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toModel(): CardModel = CardModel(
        id = id,
        name = name,
        lastDigits = lastDigits,
        limit = limit,
        isFavorite = isFavorite,
        isActive = isActive,
        dayLimit = dayLimit,
        nightLimit = nightLimit,
        brand = brand,
        expiry = expiry,
        bankColor = bankColor,
        bankName = bankName,
        isTemporary = isTemporary,
        createdAt = createdAt
    )

    companion object {
        fun fromModel(model: CardModel, accountId: Long): CardEntity = CardEntity(
            id = model.id,
            accountId = accountId,
            name = model.name,
            lastDigits = model.lastDigits,
            limit = model.limit,
            isFavorite = model.isFavorite,
            isActive = model.isActive,
            dayLimit = model.dayLimit,
            nightLimit = model.nightLimit,
            brand = model.brand,
            expiry = model.expiry,
            bankColor = model.bankColor,
            bankName = model.bankName,
            isTemporary = model.isTemporary,
            createdAt = model.createdAt
        )
    }
}
