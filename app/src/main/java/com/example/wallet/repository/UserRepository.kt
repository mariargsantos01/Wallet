package com.example.wallet.repository

import com.example.wallet.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<UserModel?>
    suspend fun login(email: String, password: String): UserModel
    suspend fun signUp(name: String, email: String, password: String): UserModel
    suspend fun getCurrentUser(): UserModel?
    suspend fun logout()
    suspend fun deleteAccount()
}
