package com.example.wallet.repository

import com.example.wallet.model.CardModel
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    /** Observa os cartões do usuário corrente em tempo real. */
    fun observeCards(): Flow<List<CardModel>>

    suspend fun getCards(): List<CardModel>
    suspend fun getCardById(id: String): CardModel?
    suspend fun addCard(card: CardModel)
    suspend fun updateCard(card: CardModel)
    suspend fun deleteCard(id: String)
    suspend fun setFavorite(id: String, isFavorite: Boolean)
    suspend fun setActive(id: String, isActive: Boolean)
    suspend fun updateLimits(id: String, dayLimit: Double, nightLimit: Double)
    suspend fun hasCards(): Boolean
}
