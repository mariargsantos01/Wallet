package com.example.wallet.repository

import com.example.wallet.data.mock.MockData
import com.example.wallet.model.UserModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserRepository : UserRepository {

    private val _current = MutableStateFlow<UserModel?>(null)

    override fun observeCurrentUser(): Flow<UserModel?> = _current.asStateFlow()

    override suspend fun login(email: String, password: String): UserModel {
        delay(500)
        return MockData.user.also { _current.value = it }
    }

    override suspend fun signUp(name: String, email: String, password: String): UserModel {
        val u = UserModel(id = 1L, name = name, email = email)
        _current.value = u
        return u
    }

    override suspend fun getCurrentUser(): UserModel? = _current.value

    override suspend fun logout() { /* mantém */ }

    override suspend fun deleteAccount() { _current.value = null }
}
