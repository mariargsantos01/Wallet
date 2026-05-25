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

data class CreateCardForm(
    val name: String = "",
    val cardType: String = "Black",
    val limit: String = ""
)

class CreateCardViewModel(
    private val cardRepository: CardRepository = ServiceLocator.cardRepository
) : ViewModel() {

    private val _form = MutableStateFlow(CreateCardForm())
    val form: StateFlow<CreateCardForm> = _form.asStateFlow()

    private val _uiState = MutableStateFlow(UiState<Boolean>())
    val uiState: StateFlow<UiState<Boolean>> = _uiState.asStateFlow()

    fun onNameChange(value: String) { _form.value = _form.value.copy(name = value) }
    fun onCardTypeChange(value: String) { _form.value = _form.value.copy(cardType = value) }
    fun onLimitChange(value: String) { _form.value = _form.value.copy(limit = value) }

    fun submit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            val f = _form.value
            val lastDigits = (1000..9999).random().toString()
            val card = CardModel(
                name = "Wallet ${f.cardType}",
                lastDigits = lastDigits,
                limit = f.limit.toDoubleOrNull() ?: 1000.0
            )
            cardRepository.addCard(card)
            _uiState.value = UiState(data = true)
            onSuccess()
        }
    }
}
