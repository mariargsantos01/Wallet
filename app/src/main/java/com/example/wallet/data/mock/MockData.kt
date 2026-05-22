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
        id = "u-1",
        name = "Maria Silva",
        email = "maria.silva@email.com"
    )

    /** Lista mutável — começa vazia; o usuário cria cartões pelo fluxo. */
    val cards: MutableList<CardModel> = mutableListOf()

    val purchases: List<PurchaseModel> = listOf(
        PurchaseModel(id = "p-1", title = "Supermercado",   amount = 245.90, date = "12/05/2026"),
        PurchaseModel(id = "p-2", title = "Streaming",      amount =  39.90, date = "10/05/2026"),
        PurchaseModel(id = "p-3", title = "Restaurante",    amount = 128.50, date = "08/05/2026"),
        PurchaseModel(id = "p-4", title = "Combustível",    amount = 200.00, date = "05/05/2026"),
        PurchaseModel(id = "p-5", title = "Farmácia",       amount =  76.20, date = "03/05/2026")
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
