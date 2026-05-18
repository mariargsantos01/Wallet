package com.example.wallet.repository

import com.example.wallet.data.remote.*
import com.example.wallet.utils.NetworkModule
import retrofit2.Response

class AuthRepository(
    private val authService: AuthService = NetworkModule.authService
) {
    suspend fun login(request: LoginRequest): Response<JwtResponse> {
        return authService.login(request)
    }

    suspend fun createUser(request: UserRequestDTO): Response<UserResponseDTO> {
        return authService.createUser(request)
    }

    suspend fun requestPasswordReset(email: String): Response<Unit> {
        return authService.requestPasswordReset(PasswordResetRequestDTO(email))
    }

    suspend fun confirmPasswordReset(request: PasswordResetConfirmDTO): Response<Unit> {
        return authService.confirmPasswordReset(request)
    }

    suspend fun refresh(refreshToken: String): Response<JwtResponse> {
        return authService.refresh(RefreshTokenRequest(refreshToken))
    }

    suspend fun logout(refreshToken: String?): Response<Unit> {
        return authService.logout(refreshToken?.let { RefreshTokenRequest(it) })
    }
}
