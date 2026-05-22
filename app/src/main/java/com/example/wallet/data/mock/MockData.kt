package com.example.wallet.data.mock

import androidx.compose.ui.graphics.Color
import com.example.wallet.model.BankAccount
import com.example.wallet.model.CardModel
import com.example.wallet.model.PurchaseModel
import com.example.wallet.model.UserModel

/**
 * Mocks simples utilizados pelas implementações fake dos repositories.
 * Substituir por dados reais (Retrofit/Room/DataStore) futuramente.
 */
object MockData {

    val user: UserModel = UserModel(
        id = 1L,
        name = "Maria Silva",
        email = "maria.silva@email.com"
    )

    /** Lista mutável — começa vazia; o usuário cria cartões pelo fluxo. */
    val cards: MutableList<CardModel> = mutableListOf()

    val purchases: List<PurchaseModel> = listOf(
        PurchaseModel(id = 1L, title = "Supermercado",   amount = 245.90, date = "12/05/2026"),
        PurchaseModel(id = 2L, title = "Streaming",      amount =  39.90, date = "10/05/2026"),
        PurchaseModel(id = 3L, title = "Restaurante",    amount = 128.50, date = "08/05/2026"),
        PurchaseModel(id = 4L, title = "Combustível",    amount = 200.00, date = "05/05/2026"),
        PurchaseModel(id = 5L, title = "Farmácia",       amount =  76.20, date = "03/05/2026")
    )

    val banks: MutableList<BankAccount> = mutableListOf(
        BankAccount("Nubank", Color(0xFF8A05BE), false, listOf("Mastercard")),
        BankAccount("Itaú", Color(0xFFFF7900), false, listOf("Visa", "Mastercard", "Elo")),
        BankAccount("Bradesco", Color(0xFFCC092F), false, listOf("Visa", "Mastercard", "Elo")),
        BankAccount("Santander", Color(0xFFEC0000), false, listOf("Visa", "Mastercard")),
        BankAccount("Banco do Brasil", Color(0xFFF9D70B), false, listOf("Visa", "Mastercard", "Elo")),
        BankAccount("Caixa", Color(0xFF005CA5), false, listOf("Visa", "Elo"))
    )

    const val BALANCE: Double = 12_345.67
}
