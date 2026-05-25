package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.data.remote.LoginRequest
import com.example.wallet.repository.AuthRepository
import com.example.wallet.repository.CardRepository
import com.example.wallet.repository.UserRepository
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginResult(val hasCards: Boolean)

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val cardRepository: CardRepository = ServiceLocator.cardRepository,
    private val userRepository: UserRepository = ServiceLocator.userRepository,
    private val sessionManager: com.example.wallet.utils.SessionManager = ServiceLocator.sessionManager
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
        if (_username.value.isBlank()) {
            _uiState.value = UiState(error = "Informe o usuário")
            return
        }
        if (_password.value.isBlank()) {
            _uiState.value = UiState(error = "Informe a senha")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            try {
                val response = authRepository.login(
                    LoginRequest(username.value, password.value)
                )

                if (response.isSuccessful) {
                    val jwt = response.body()
                    // Persiste a conta localmente (Room) para que cartões/transações
                    // possam ser associados e recuperados após logout/relogin.
                    userRepository.login(username.value, password.value)

                    // Salva informações do usuário na sessão
                    val displayName = jwt?.fullName?.ifBlank { null }
                        ?: username.value.replaceFirstChar { it.uppercase() }
                    val userEmail = jwt?.email?.ifBlank { null } ?: username.value
                    val userName = jwt?.username?.ifBlank { null } ?: username.value
                    sessionManager.setUserInfo(
                        username = userName,
                        displayName = displayName,
                        email = userEmail
                    )

                    // Tenta inserir compras de exemplo (se ainda não existem)
                    ServiceLocator.trySeedPurchases()
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
