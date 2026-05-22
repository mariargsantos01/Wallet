package com.example.wallet.repository

import com.example.wallet.model.CardModel
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    /** Observa os cartões do usuário corrente em tempo real. */
    fun observeCards(): Flow<List<CardModel>>

    suspend fun getCards(): List<CardModel>
    suspend fun getCardById(id: Long): CardModel?
    suspend fun addCard(card: CardModel): Long
    suspend fun updateCard(card: CardModel)
    suspend fun deleteCard(id: Long)
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
    suspend fun setActive(id: Long, isActive: Boolean)
    suspend fun updateLimits(id: Long, dayLimit: Double, nightLimit: Double)
    suspend fun hasCards(): Boolean
}
