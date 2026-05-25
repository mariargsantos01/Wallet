package com.example.wallet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.wallet.model.PurchaseCategory
import com.example.wallet.model.PurchaseModel

/**
 * Entidade Room de uma transação/compra.
 *
 * Pode estar vinculada a uma conta (obrigatório) e, opcionalmente,
 * a um cartão específico. Exclusão em cascata da conta apaga as transações.
 */
@Entity(
    tableName = "purchases",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["accountId"]), Index(value = ["cardId"])]
)
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val accountId: Long,
    val cardId: Long? = null,
    val title: String,
    val amount: Double,
    val date: String,
    val category: String = PurchaseCategory.OTHER.name,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toModel(): PurchaseModel = PurchaseModel(
        id = id,
        title = title,
        amount = amount,
        date = date,
        category = try { PurchaseCategory.valueOf(category) } catch (_: Exception) { PurchaseCategory.OTHER }
    )
}
