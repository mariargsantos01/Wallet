package com.example.wallet.repository

import com.example.wallet.model.CardModel

interface CardRepository {
    suspend fun getCards(): List<CardModel>
    suspend fun getCardById(id: String): CardModel?
    suspend fun addCard(card: CardModel)
    suspend fun deleteCard(id: String)
    suspend fun hasCards(): Boolean
}
