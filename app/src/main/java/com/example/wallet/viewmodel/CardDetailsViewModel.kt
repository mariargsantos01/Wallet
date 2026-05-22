package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.model.CardModel
import com.example.wallet.repository.CardRepository
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardDetailsViewModel(
    private val cardRepository: CardRepository = ServiceLocator.cardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<CardModel>())
    val uiState: StateFlow<UiState<CardModel>> = _uiState.asStateFlow()

    fun loadCard(cardId: String) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            try {
                val card = cardRepository.getCardById(cardId)
                _uiState.value = if (card != null) {
                    UiState(data = card)
                } else {
                    UiState(error = "Cartão não encontrado")
                }
            } catch (e: Exception) {
                _uiState.value = UiState(error = e.message ?: "Erro ao carregar")
            }
        }
    }

    fun blockCard() {
        val card = _uiState.value.data ?: return
        viewModelScope.launch {
            cardRepository.setActive(card.id, !card.isActive)
            _uiState.value = UiState(data = card.copy(isActive = !card.isActive))
        }
    }

    fun changeLimit(dayLimit: Double, nightLimit: Double) {
        val card = _uiState.value.data ?: return
        viewModelScope.launch {
            cardRepository.updateLimits(card.id, dayLimit, nightLimit)
            _uiState.value = UiState(data = card.copy(dayLimit = dayLimit, nightLimit = nightLimit))
        }
    }
}

