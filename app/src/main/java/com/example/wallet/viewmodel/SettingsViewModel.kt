package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.repository.UserRepository
import com.example.wallet.utils.ServiceLocator
import com.example.wallet.utils.ThemeMode
import com.example.wallet.utils.ThemePreferences
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository = ServiceLocator.userRepository,
    private val themePreferences: ThemePreferences = ServiceLocator.themePreferences
) : ViewModel() {

    /** Modo de tema atual observável. */
    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode

    /** Altera o modo de tema. */
    fun setThemeMode(mode: ThemeMode) {
        themePreferences.setThemeMode(mode)
    }

    fun logout(onCompleted: () -> Unit) {
        viewModelScope.launch {
            ServiceLocator.stopPurchaseSimulator()
            userRepository.logout()
            onCompleted()
        }
    }

    fun deleteAccount(onCompleted: () -> Unit) {
        viewModelScope.launch {
            userRepository.deleteAccount()
            onCompleted()
        }
    }
}
