package com.example.wallet.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class JwtResponse(
    val token: String,
    val refreshToken: String
)

@Serializable
data class UserRequestDTO(
    val fullName: String,
    val username: String,
    val email: String,
    val password: String,
    val role: String = "USER"
)

@Serializable
data class UserResponseDTO(
    val id: Long,
    val fullName: String,
    val role: String,
    val username: String,
    val email: String,
    val active: Boolean
)

@Serializable
data class ErrorResponseDTO(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: String
)

@Serializable
data class PasswordResetRequestDTO(
    val email: String
)

@Serializable
data class PasswordResetConfirmDTO(
    val token: String,
    val newPassword: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)
