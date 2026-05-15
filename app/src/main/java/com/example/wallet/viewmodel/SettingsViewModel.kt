package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.repository.UserRepository
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository = ServiceLocator.userRepository
) : ViewModel() {

    fun logout(onCompleted: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onCompleted()
        }
    }
}

