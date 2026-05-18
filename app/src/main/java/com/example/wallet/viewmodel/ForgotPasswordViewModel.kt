package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.data.remote.PasswordResetConfirmDTO
import com.example.wallet.repository.AuthRepository
import com.example.wallet.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _uiState = MutableStateFlow(UiState<Unit>())
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    fun onEmailChange(value: String) { _email.value = value }
    fun onTokenChange(value: String) { _token.value = value }
    fun onNewPasswordChange(value: String) { _newPassword.value = value }

    fun requestPasswordReset(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            try {
                val response = authRepository.requestPasswordReset(_email.value)
                if (response.isSuccessful) {
                    _uiState.value = UiState(data = Unit)
                    onSuccess()
                } else {
                    _uiState.value = UiState(error = "Erro ao solicitar recuperação")
                }
            } catch (e: Exception) {
                _uiState.value = UiState(error = "Erro de conexão: ${e.message}")
            }
        }
    }

    fun confirmPasswordReset(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            try {
                val request = PasswordResetConfirmDTO(
                    token = _token.value,
                    newPassword = _newPassword.value
                )
                val response = authRepository.confirmPasswordReset(request)
                if (response.isSuccessful) {
                    _uiState.value = UiState(data = Unit)
                    onSuccess()
                } else {
                    _uiState.value = UiState(error = "Token inválido ou expirado")
                }
            } catch (e: Exception) {
                _uiState.value = UiState(error = "Erro de conexão: ${e.message}")
            }
        }
    }
}
