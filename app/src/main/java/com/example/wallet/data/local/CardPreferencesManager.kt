package com.example.wallet.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.wallet.ui.components.cardmanagement.CardManagementState

/**
 * Gerencia a persistência dos estados de gerenciamento de cartão
 * usando SharedPreferences. Os dados persistem entre sessões e logout/login.
 */
class CardPreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveCardState(cardId: Long, state: CardManagementState) {
        prefs.edit()
            .putBoolean(key(cardId, KEY_FAVORITE), state.isFavorite)
            .putBoolean(key(cardId, KEY_ACTIVE), state.isActive)
            .putFloat(key(cardId, KEY_DAY_LIMIT), state.dayLimit)
            .putFloat(key(cardId, KEY_NIGHT_LIMIT), state.nightLimit)
            .putBoolean(key(cardId, KEY_SHOW_DATA), state.showData)
            .putBoolean(key(cardId, KEY_HAS_SAVED), true)
            .apply()
    }

    fun getCardState(cardId: Long): CardManagementState? {
        if (!prefs.getBoolean(key(cardId, KEY_HAS_SAVED), false)) {
            return null // Nenhum estado salvo → usar padrão
        }
        return CardManagementState(
            isFavorite = prefs.getBoolean(key(cardId, KEY_FAVORITE), false),
            isActive = prefs.getBoolean(key(cardId, KEY_ACTIVE), true),
            dayLimit = prefs.getFloat(key(cardId, KEY_DAY_LIMIT), 5000f),
            nightLimit = prefs.getFloat(key(cardId, KEY_NIGHT_LIMIT), 2000f),
            showData = prefs.getBoolean(key(cardId, KEY_SHOW_DATA), false)
        )
    }

    fun removeCardState(cardId: Long) {
        prefs.edit()
            .remove(key(cardId, KEY_FAVORITE))
            .remove(key(cardId, KEY_ACTIVE))
            .remove(key(cardId, KEY_DAY_LIMIT))
            .remove(key(cardId, KEY_NIGHT_LIMIT))
            .remove(key(cardId, KEY_SHOW_DATA))
            .remove(key(cardId, KEY_HAS_SAVED))
            .apply()
    }

    private fun key(cardId: Long, field: String) = "${cardId}_$field"

    companion object {
        private const val PREFS_NAME = "card_management_prefs"
        private const val KEY_FAVORITE = "favorite"
        private const val KEY_ACTIVE = "active"
        private const val KEY_DAY_LIMIT = "day_limit"
        private const val KEY_NIGHT_LIMIT = "night_limit"
        private const val KEY_SHOW_DATA = "show_data"
        private const val KEY_HAS_SAVED = "has_saved"
    }
}
