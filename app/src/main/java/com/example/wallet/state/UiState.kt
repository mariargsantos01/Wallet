package com.example.wallet.state

/**
 * Estado de UI genérico utilizado pelos ViewModels.
 *
 * @param isLoading indica se há uma operação em andamento
 * @param data dados carregados
 * @param error mensagem de erro, caso exista
 */
data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null
)

