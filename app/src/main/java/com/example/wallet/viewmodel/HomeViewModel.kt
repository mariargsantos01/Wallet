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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MyCardsViewModel(
    private val cardRepository: CardRepository = ServiceLocator.cardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<CardModel>>())
    val uiState: StateFlow<UiState<List<CardModel>>> = _uiState.asStateFlow()

    init {
        observe()
        // Garante que compras de exemplo sejam inseridas se houver cartões
        ServiceLocator.trySeedPurchases()
    }

    private fun observe() {
        viewModelScope.launch {
            cardRepository.observeCards()
                .onStart { _uiState.value = UiState(isLoading = true) }
                .catch { e -> _uiState.value = UiState(error = e.message ?: "Erro ao carregar") }
                .collect { cards -> _uiState.value = UiState(data = cards) }
        }
    }

    fun load() = observe()
}
