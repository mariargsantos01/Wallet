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

class MyCardsViewModel(
    private val cardRepository: CardRepository = ServiceLocator.cardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<CardModel>>())
    val uiState: StateFlow<UiState<List<CardModel>>> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            try {
                val cards = cardRepository.getCards()
                _uiState.value = UiState(data = cards)
            } catch (e: Exception) {
                _uiState.value = UiState(error = e.message ?: "Erro ao carregar")
            }
        }
    }
}
