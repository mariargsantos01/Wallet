package com.example.wallet.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gerencia a sessão atual (qual usuário está logado).
 *
 * O id do usuário corrente é persistido em [SharedPreferences] para sobreviver
 * a reinícios do app, e exposto como [StateFlow] para observação reativa.
 *
 * Cada conta no banco tem seus próprios cartões/compras, isolados por
 * `accountId` (foreign key). Trocar de sessão = trocar o filtro reativo
 * usado pelos repositórios.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentUserId = MutableStateFlow(
        prefs.getLong(KEY_USER_ID, -1L).takeIf { it != -1L }
    )
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private val _displayName = MutableStateFlow(
        prefs.getString(KEY_DISPLAY_NAME, null)
    )
    val displayName: StateFlow<String?> = _displayName.asStateFlow()

    private val _username = MutableStateFlow(
        prefs.getString(KEY_USERNAME, null)
    )
    val username: StateFlow<String?> = _username.asStateFlow()

    private val _email = MutableStateFlow(
        prefs.getString(KEY_EMAIL, null)
    )
    val email: StateFlow<String?> = _email.asStateFlow()

    fun setCurrentUser(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
        _currentUserId.value = userId
    }

    fun setUserInfo(username: String, displayName: String, email: String) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_DISPLAY_NAME, displayName)
            .putString(KEY_EMAIL, email)
            .apply()
        _username.value = username
        _displayName.value = displayName
        _email.value = email
    }

    fun clear() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USERNAME)
            .remove(KEY_DISPLAY_NAME)
            .remove(KEY_EMAIL)
            .apply()
        _currentUserId.value = null
        _username.value = null
        _displayName.value = null
        _email.value = null
    }

    fun getCurrentUserId(): Long? = _currentUserId.value

    companion object {
        private const val PREFS_NAME = "wallet_session"
        private const val KEY_USER_ID = "current_user_id"
        private const val KEY_USERNAME = "current_username"
        private const val KEY_DISPLAY_NAME = "current_display_name"
        private const val KEY_EMAIL = "current_email"
    }
}
