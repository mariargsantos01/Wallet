package com.example.wallet.repository

import com.example.wallet.model.UserModel

interface UserRepository {
    suspend fun login(email: String, password: String): UserModel
    suspend fun getCurrentUser(): UserModel?
    suspend fun logout()
}

