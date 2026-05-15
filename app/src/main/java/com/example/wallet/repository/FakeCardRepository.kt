package com.example.wallet.repository

import com.example.wallet.data.mock.MockData
import com.example.wallet.model.CardModel
import kotlinx.coroutines.delay

class FakeCardRepository : CardRepository {

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
    }

    override suspend fun hasCards(): Boolean {
        delay(100)
        return MockData.cards.isNotEmpty()
    }
}
