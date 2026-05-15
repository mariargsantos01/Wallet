package com.example.wallet.repository

import com.example.wallet.data.mock.MockData
import com.example.wallet.model.UserModel
import kotlinx.coroutines.delay

class FakeUserRepository : UserRepository {

    private var current: UserModel? = null

    override suspend fun login(email: String, password: String): UserModel {
        delay(500)
        // Sem autenticação real: apenas retorna usuário mockado.
        return MockData.user.also { current = it }
    }

    override suspend fun getCurrentUser(): UserModel? = current

    override suspend fun logout() {
        current = null
    }
}

