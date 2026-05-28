package com.example.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.data.local.CardPreferencesManager
import com.example.wallet.model.CardModel
import com.example.wallet.repository.CardRepository
import com.example.wallet.ui.components.cardmanagement.CardManagementState
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State imutável para o gerenciamento de cartão.
 */
data class CardManagementUiState(
    val isManagementSheetOpen: Boolean = false,
    val showDeleteWarningDialog: Boolean = false,
    val showFinalDeleteDialog: Boolean = false,
    val selectedCard: CardModel? = null,
    val managementState: CardManagementState = CardManagementState()
)

/**
 * Eventos one-shot emitidos para a UI.
 */
sealed class CardManagementEvent {
    data object CardDeletedSuccessfully : CardManagementEvent()
}

class CardManagementViewModel(
    private val cardRepository: CardRepository = ServiceLocator.cardRepository,
    private val preferencesManager: CardPreferencesManager = ServiceLocator.cardPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardManagementUiState())
    val uiState: StateFlow<CardManagementUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CardManagementEvent>()
    val events: SharedFlow<CardManagementEvent> = _events.asSharedFlow()

    // --- Sheet ---

    fun openManagement(card: CardModel) {
        // Usa os dados reais do cartão (Room) como fonte da verdade
        // e complementa com dados visuais do SharedPreferences (showData)
        val savedState = preferencesManager.getCardState(card.id)
        val state = CardManagementState(
            isFavorite = card.isFavorite,
            isActive = card.isActive,
            dayLimit = card.dayLimit.toFloat(),
            nightLimit = card.nightLimit.toFloat(),
            showData = savedState?.showData ?: false
        )
        _uiState.update {
            it.copy(
                isManagementSheetOpen = true,
                selectedCard = card,
                managementState = state
            )
        }
    }

    fun closeManagement() {
        // Persistir estado ao fechar
        val currentCard = _uiState.value.selectedCard
        if (currentCard != null) {
            preferencesManager.saveCardState(currentCard.id, _uiState.value.managementState)
        }
        _uiState.update {
            it.copy(
                isManagementSheetOpen = false,
                showDeleteWarningDialog = false,
                showFinalDeleteDialog = false
            )
        }
    }

    fun updateManagementState(state: CardManagementState) {
        val previousState = _uiState.value.managementState
        _uiState.update { it.copy(managementState = state) }
        // Persistir imediatamente a cada mudança
        val currentCard = _uiState.value.selectedCard
        if (currentCard != null) {
            preferencesManager.saveCardState(currentCard.id, state)

            // Atualizar Room quando isActive ou isFavorite mudar
            viewModelScope.launch {
                if (state.isActive != previousState.isActive) {
                    cardRepository.setActive(currentCard.id, state.isActive)
                }
                if (state.isFavorite != previousState.isFavorite) {
                    cardRepository.setFavorite(currentCard.id, state.isFavorite)
                }
                if (state.dayLimit != previousState.dayLimit || state.nightLimit != previousState.nightLimit) {
                    cardRepository.updateLimits(currentCard.id, state.dayLimit.toDouble(), state.nightLimit.toDouble())
                }
            }
        }
    }

    // --- Delete Flow ---

    fun onDeleteCardClicked() {
        _uiState.update { it.copy(showDeleteWarningDialog = true) }
    }

    fun dismissDeleteWarning() {
        _uiState.update { it.copy(showDeleteWarningDialog = false) }
    }

    fun onContinueWithDeletion() {
        _uiState.update {
            it.copy(
                showDeleteWarningDialog = false,
                showFinalDeleteDialog = true
            )
        }
    }

    fun dismissFinalDelete() {
        _uiState.update { it.copy(showFinalDeleteDialog = false) }
    }

    fun onConfirmDeletePermanently() {
        val card = _uiState.value.selectedCard ?: return
        viewModelScope.launch {
            try {
                cardRepository.deleteCard(card.id)
                // Remover dados persistidos do cartão excluído
                preferencesManager.removeCardState(card.id)
                _uiState.update {
                    it.copy(
                        isManagementSheetOpen = false,
                        showDeleteWarningDialog = false,
                        showFinalDeleteDialog = false,
                        selectedCard = null
                    )
                }
                _events.emit(CardManagementEvent.CardDeletedSuccessfully)
            } catch (_: Exception) {
                // Em produção: tratar erro
            }
        }
    }
}
