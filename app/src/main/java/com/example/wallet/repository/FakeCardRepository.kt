package com.example.wallet.repository

import com.example.wallet.data.mock.MockData
import com.example.wallet.model.CardModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementação em memória — usada apenas em previews/testes simples.
 * A persistência real do app é feita por [RoomCardRepository].
 */
class FakeCardRepository : CardRepository {

    private val _cards = MutableStateFlow(MockData.cards.toList())

    override fun observeCards(): Flow<List<CardModel>> = _cards.asStateFlow()

    override suspend fun getCards(): List<CardModel> {
        delay(300)
        return MockData.cards.toList()
    }

    override suspend fun getCardById(id: String): CardModel? {
        delay(200)
        return MockData.cards.firstOrNull { it.id == id }
    }

    override suspend fun addCard(card: CardModel) {
        delay(300)
        MockData.cards.add(card)
        _cards.value = MockData.cards.toList()
    }

    override suspend fun updateCard(card: CardModel) {
        val idx = MockData.cards.indexOfFirst { it.id == card.id }
        if (idx >= 0) {
            MockData.cards[idx] = card
            _cards.value = MockData.cards.toList()
        }
    }

    override suspend fun deleteCard(id: String) {
        MockData.cards.removeAll { it.id == id }
        _cards.value = MockData.cards.toList()
    }

    override suspend fun setFavorite(id: String, isFavorite: Boolean) {
        val idx = MockData.cards.indexOfFirst { it.id == id }
        if (idx >= 0) {
            MockData.cards[idx] = MockData.cards[idx].copy(isFavorite = isFavorite)
            _cards.value = MockData.cards.toList()
        }
    }

    override suspend fun setActive(id: String, isActive: Boolean) {
        val idx = MockData.cards.indexOfFirst { it.id == id }
        if (idx >= 0) {
            MockData.cards[idx] = MockData.cards[idx].copy(isActive = isActive)
            _cards.value = MockData.cards.toList()
        }
    }

    override suspend fun updateLimits(id: String, dayLimit: Double, nightLimit: Double) {
        val idx = MockData.cards.indexOfFirst { it.id == id }
        if (idx >= 0) {
            MockData.cards[idx] = MockData.cards[idx].copy(
                dayLimit = dayLimit,
                nightLimit = nightLimit
            )
            _cards.value = MockData.cards.toList()
        }
    }

    override suspend fun deleteCard(id: String) {
        delay(300)
        MockData.cards.removeAll { it.id == id }
    }

    override suspend fun hasCards(): Boolean {
        delay(100)
        return MockData.cards.isNotEmpty()
    }
}
