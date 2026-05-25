package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import com.example.wallet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditProfileViewModel(
    private val sessionManager: SessionManager = ServiceLocator.sessionManager
) : ViewModel() {

    private val _displayName = MutableStateFlow(sessionManager.displayName.value ?: "")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _email = MutableStateFlow(sessionManager.email.value ?: "")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _uiState = MutableStateFlow(UiState<Unit>())
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    fun onDisplayNameChange(value: String) { _displayName.value = value }
    fun onEmailChange(value: String) { _email.value = value }

    fun save() {
        val name = _displayName.value.trim()
        val emailVal = _email.value.trim()

        if (name.isBlank()) {
            _uiState.value = UiState(error = "Informe seu nome")
            return
        }
        if (emailVal.isBlank() || !emailVal.contains("@")) {
            _uiState.value = UiState(error = "Informe um email válido")
            return
        }

        sessionManager.setUserInfo(
            username = sessionManager.username.value ?: emailVal,
            displayName = name,
            email = emailVal
        )
        _uiState.value = UiState(data = Unit)
    }
}

