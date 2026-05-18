package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.data.remote.LoginRequest
import com.example.wallet.repository.AuthRepository
import com.example.wallet.repository.CardRepository
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginResult(val hasCards: Boolean)

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val cardRepository: CardRepository = ServiceLocator.cardRepository
) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow(UiState<LoginResult>())
    val uiState: StateFlow<UiState<LoginResult>> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) { _username.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun login(onSuccess: (hasCards: Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            try {
                val response = authRepository.login(
                    LoginRequest(username.value, password.value)
                )

                if (response.isSuccessful) {
                    // Aqui você salvaria os tokens (SharedPreferences/DataStore)
                    // Por enquanto, seguimos o fluxo original de verificar cartões
                    val hasCards = cardRepository.hasCards()
                    _uiState.value = UiState(data = LoginResult(hasCards))
                    onSuccess(hasCards)
                } else {
                    _uiState.value = UiState(error = "Usuário ou senha inválidos")
                }
            } catch (e: Exception) {
                _uiState.value = UiState(error = "Erro de conexão: ${e.message}")
            }
        }
    }
}
