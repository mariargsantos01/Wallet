package com.example.wallet.model

/**
 * Categorias de compra disponíveis.
 */
enum class PurchaseCategory(val label: String, val icon: String) {
    FOOD("Alimentação", "restaurant"),
    TRANSPORT("Transporte", "directions_car"),
    ENTERTAINMENT("Entretenimento", "movie"),
    HEALTH("Saúde", "local_pharmacy"),
    SHOPPING("Compras", "shopping_bag"),
    BILLS("Contas", "receipt"),
    SUBSCRIPTION("Assinatura", "subscriptions"),
    OTHER("Outros", "more_horiz")
}

data class PurchaseModel(
    val id: Long = 0L,
    val title: String,
    val amount: Double,
    val date: String,
    val category: PurchaseCategory = PurchaseCategory.OTHER
)
