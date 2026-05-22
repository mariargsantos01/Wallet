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

    private val _currentUserId = MutableStateFlow(prefs.getString(KEY_USER_ID, null))
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    fun setCurrentUser(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
        _currentUserId.value = userId
    }

    fun clear() {
        prefs.edit().remove(KEY_USER_ID).apply()
        _currentUserId.value = null
    }

    fun getCurrentUserId(): String? = _currentUserId.value

    companion object {
        private const val PREFS_NAME = "wallet_session"
        private const val KEY_USER_ID = "current_user_id"
    }
}

