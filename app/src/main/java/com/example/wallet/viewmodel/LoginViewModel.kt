package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.repository.CardRepository
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Resultado do login: indica se o usuário já possui cartões.
 */
data class LoginResult(val hasCards: Boolean)

class LoginViewModel(
    private val cardRepository: CardRepository = ServiceLocator.cardRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow(UiState<LoginResult>())
    val uiState: StateFlow<UiState<LoginResult>> = _uiState.asStateFlow()

    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    /**
     * Faz login mock e verifica se o usuário já tem cartões.
     * @param onSuccess recebe true se já tem cartões, false caso contrário.
     */
    fun login(onSuccess: (hasCards: Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            delay(400) // simula chamada
            val hasCards = cardRepository.hasCards()
            _uiState.value = UiState(data = LoginResult(hasCards))
            onSuccess(hasCards)
        }
    }
}
