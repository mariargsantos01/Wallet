package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.model.PurchaseModel
import com.example.wallet.repository.PurchaseRepository
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PurchasesViewModel(
    private val purchaseRepository: PurchaseRepository = ServiceLocator.purchaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<PurchaseModel>>())
    val uiState: StateFlow<UiState<List<PurchaseModel>>> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            try {
                val items = purchaseRepository.getPurchases()
                _uiState.value = UiState(data = items)
            } catch (e: Exception) {
                _uiState.value = UiState(error = e.message ?: "Erro ao carregar")
            }
        }
    }
}

