package com.example.wallet.utils

import com.example.wallet.repository.CardRepository
import com.example.wallet.repository.FakeCardRepository
import com.example.wallet.repository.FakePurchaseRepository
import com.example.wallet.repository.FakeUserRepository
import com.example.wallet.repository.PurchaseRepository
import com.example.wallet.repository.UserRepository

/**
 * Service Locator simples.
 *
 * Substituir por Hilt/Koin quando a injeção de dependências real for adicionada.
 */
object ServiceLocator {
    val cardRepository: CardRepository by lazy { FakeCardRepository() }
    val purchaseRepository: PurchaseRepository by lazy { FakePurchaseRepository() }
    val userRepository: UserRepository by lazy { FakeUserRepository() }
}

