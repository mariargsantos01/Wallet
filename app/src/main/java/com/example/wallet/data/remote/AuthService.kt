package com.example.wallet.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<JwtResponse>

    @POST("/api/users/createUser")
    suspend fun createUser(@Body request: UserRequestDTO): Response<UserResponseDTO>

    @POST("/api/auth/password-reset/request")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequestDTO): Response<Unit>

    @POST("/api/auth/password-reset/confirm")
    suspend fun confirmPasswordReset(@Body request: PasswordResetConfirmDTO): Response<Unit>

    @POST("/api/auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): Response<JwtResponse>

    @POST("/api/auth/logout")
    suspend fun logout(@Body request: RefreshTokenRequest?): Response<Unit>
}
