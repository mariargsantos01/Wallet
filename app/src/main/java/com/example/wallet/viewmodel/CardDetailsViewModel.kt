package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.model.CardModel
import com.example.wallet.repository.CardRepository
import com.example.wallet.repository.PurchaseRepository
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CardDetailsUiState(
    val card: CardModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalSpent: Double = 0.0,
    val showLimitDialog: Boolean = false,
    val newDayLimit: String = "",
    val newNightLimit: String = "",
    val snackMessage: String? = null
)

class CardDetailsViewModel(
    private val cardRepository: CardRepository = ServiceLocator.cardRepository,
    private val purchaseRepository: PurchaseRepository = ServiceLocator.purchaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CardDetailsUiState())
    val state: StateFlow<CardDetailsUiState> = _state.asStateFlow()

    // Compatibilidade com tela existente
    private val _uiState = MutableStateFlow(UiState<CardModel>())
    val uiState: StateFlow<UiState<CardModel>> = _uiState.asStateFlow()

    fun loadCard(cardId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            _uiState.value = UiState(isLoading = true)
            try {
                val card = cardRepository.getCardById(cardId)
                val total = purchaseRepository.getTotalByCard(cardId)
                if (card != null) {
                    _state.update { it.copy(card = card, isLoading = false, totalSpent = total) }
                    _uiState.value = UiState(data = card)
                } else {
                    _state.update { it.copy(isLoading = false, error = "Cartão não encontrado") }
                    _uiState.value = UiState(error = "Cartão não encontrado")
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                _uiState.value = UiState(error = e.message ?: "Erro ao carregar")
            }
        }
    }

    fun blockCard() {
        val card = _state.value.card ?: return
        viewModelScope.launch {
            cardRepository.setActive(card.id, !card.isActive)
            val updated = card.copy(isActive = !card.isActive)
            _state.update { it.copy(card = updated, snackMessage = if (updated.isActive) "Cartão desbloqueado" else "Cartão bloqueado") }
            _uiState.value = UiState(data = updated)
        }
    }

    fun toggleFavorite() {
        val card = _state.value.card ?: return
        viewModelScope.launch {
            cardRepository.setFavorite(card.id, !card.isFavorite)
            val updated = card.copy(isFavorite = !card.isFavorite)
            _state.update { it.copy(card = updated, snackMessage = if (updated.isFavorite) "Cartão favoritado" else "Removido dos favoritos") }
            _uiState.value = UiState(data = updated)
        }
    }

    fun showLimitDialog() {
        val card = _state.value.card ?: return
        _state.update { it.copy(
            showLimitDialog = true,
            newDayLimit = card.dayLimit.toLong().toString(),
            newNightLimit = card.nightLimit.toLong().toString()
        )}
    }

    fun dismissLimitDialog() {
        _state.update { it.copy(showLimitDialog = false) }
    }

    fun onDayLimitChange(value: String) {
        _state.update { it.copy(newDayLimit = value) }
    }

    fun onNightLimitChange(value: String) {
        _state.update { it.copy(newNightLimit = value) }
    }

    fun confirmLimitChange() {
        val card = _state.value.card ?: return
        val day = _state.value.newDayLimit.toDoubleOrNull() ?: return
        val night = _state.value.newNightLimit.toDoubleOrNull() ?: return
        viewModelScope.launch {
            cardRepository.updateLimits(card.id, day, night)
            val updated = card.copy(dayLimit = day, nightLimit = night)
            _state.update { it.copy(card = updated, showLimitDialog = false, snackMessage = "Limites atualizados") }
            _uiState.value = UiState(data = updated)
        }
    }

    fun changeLimit(dayLimit: Double, nightLimit: Double) {
        showLimitDialog()
    }

    fun clearSnack() {
        _state.update { it.copy(snackMessage = null) }
    }
}
