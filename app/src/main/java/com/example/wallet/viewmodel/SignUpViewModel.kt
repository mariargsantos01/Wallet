package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.data.remote.UserRequestDTO
import com.example.wallet.repository.AuthRepository
import com.example.wallet.repository.UserRepository
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = ServiceLocator.userRepository
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow(UiState<Unit>())
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) { _fullName.value = value }
    fun onUsernameChange(value: String) { _username.value = value }
    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun signUp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            try {
                val request = UserRequestDTO(
                    fullName = _fullName.value,
                    username = _username.value,
                    email = _email.value,
                    password = _password.value,
                    role = "USER"
                )
                val response = authRepository.createUser(request)
                if (response.isSuccessful) {
                    userRepository.signUp(_fullName.value, _email.value, _password.value)
                    _uiState.value = UiState(data = Unit)
                    onSuccess()
                } else {
                    _uiState.value = UiState(error = "Erro ao cadastrar: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = UiState(error = "Erro de conexão: ${e.message}")
            }
        }
    }
}
