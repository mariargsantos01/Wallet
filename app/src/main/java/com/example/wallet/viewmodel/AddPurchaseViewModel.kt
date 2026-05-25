package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.model.CardModel
import com.example.wallet.model.PurchaseCategory
import com.example.wallet.model.PurchaseModel
import com.example.wallet.repository.CardRepository
import com.example.wallet.repository.PurchaseRepository
import com.example.wallet.state.UiState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPurchaseViewModel(
    private val purchaseRepository: PurchaseRepository = ServiceLocator.purchaseRepository,
    private val cardRepository: CardRepository = ServiceLocator.cardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<Unit>())
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _date = MutableStateFlow(
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date())
    )
    val date: StateFlow<String> = _date.asStateFlow()

    private val _category = MutableStateFlow(PurchaseCategory.OTHER)
    val category: StateFlow<PurchaseCategory> = _category.asStateFlow()

    private val _cards = MutableStateFlow<List<CardModel>>(emptyList())
    val cards: StateFlow<List<CardModel>> = _cards.asStateFlow()

    private val _selectedCardId = MutableStateFlow<Long?>(null)
    val selectedCardId: StateFlow<Long?> = _selectedCardId.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            cardRepository.observeCards().collect { list ->
                _cards.value = list
                if (_selectedCardId.value == null && list.isNotEmpty()) {
                    _selectedCardId.value = list.first().id
                }
            }
        }
    }

    fun onTitleChange(value: String) { _title.value = value }
    fun onAmountChange(value: String) { _amount.value = value }
    fun onDateChange(value: String) { _date.value = value }
    fun onCategoryChange(value: PurchaseCategory) { _category.value = value }
    fun onCardSelected(cardId: Long) { _selectedCardId.value = cardId }

    fun submit(onSuccess: () -> Unit) {
        val titleVal = _title.value.trim()
        val amountVal = _amount.value.replace(",", ".").toDoubleOrNull()
        val dateVal = _date.value.trim()

        if (titleVal.isBlank()) {
            _uiState.value = UiState(error = "Informe a descrição da compra")
            return
        }
        if (amountVal == null || amountVal <= 0) {
            _uiState.value = UiState(error = "Informe um valor válido")
            return
        }
        if (dateVal.length < 8) {
            _uiState.value = UiState(error = "Informe uma data válida")
            return
        }
        if (_selectedCardId.value == null) {
            _uiState.value = UiState(error = "Selecione um cartão")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            try {
                purchaseRepository.addPurchase(
                    purchase = PurchaseModel(
                        title = titleVal,
                        amount = amountVal,
                        date = dateVal,
                        category = _category.value
                    ),
                    cardId = _selectedCardId.value
                )
                _uiState.value = UiState(data = Unit)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = UiState(error = "Erro ao registrar: ${e.message}")
            }
        }
    }
}

