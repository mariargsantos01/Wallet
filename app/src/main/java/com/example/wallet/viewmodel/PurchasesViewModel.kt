package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.model.CardModel
import com.example.wallet.model.PurchaseModel
import com.example.wallet.repository.CardRepository
import com.example.wallet.repository.PurchaseRepository
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * Lista de compras filtrada **por cartão**.
 *
 * - Observa os cartões do usuário logado.
 * - Mantém um `selectedCardId` em memória; por padrão usa o primeiro cartão.
 * - Quando muda o cartão selecionado, troca o filtro reativamente.
 * - Um cartão novo sem compras retorna lista vazia.
 */
class PurchasesViewModel(
    private val cardRepository: CardRepository = ServiceLocator.cardRepository,
    private val purchaseRepository: PurchaseRepository = ServiceLocator.purchaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<PurchaseModel>>())
    val uiState: StateFlow<UiState<List<PurchaseModel>>> = _uiState.asStateFlow()

    private val _cards = MutableStateFlow<List<CardModel>>(emptyList())
    val cards: StateFlow<List<CardModel>> = _cards.asStateFlow()

    private val _selectedCardId = MutableStateFlow<String?>(null)
    val selectedCardId: StateFlow<String?> = _selectedCardId.asStateFlow()

    init {
        observeCards()
        observePurchases()
    }

    private fun observeCards() {
        viewModelScope.launch {
            cardRepository.observeCards().collect { list ->
                _cards.value = list
                // Se nenhuma seleção, ou o cartão selecionado foi removido,
                // seleciona o primeiro automaticamente.
                val currentId = _selectedCardId.value
                if (currentId == null || list.none { it.id == currentId }) {
                    _selectedCardId.value = list.firstOrNull()?.id
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observePurchases() {
        viewModelScope.launch {
            _selectedCardId
                .flatMapLatest { cardId ->
                    if (cardId == null) flowOf(emptyList())
                    else purchaseRepository.observePurchasesByCard(cardId)
                }
                .onStart { _uiState.value = UiState(isLoading = true) }
                .catch { e -> _uiState.value = UiState(error = e.message ?: "Erro ao carregar") }
                .collect { items -> _uiState.value = UiState(data = items) }
        }
    }

    fun selectCard(cardId: String) {
        _selectedCardId.value = cardId
    }

    fun load() {
        observeCards()
        observePurchases()
    }
}
