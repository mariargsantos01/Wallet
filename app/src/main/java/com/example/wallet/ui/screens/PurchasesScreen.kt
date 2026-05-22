package com.example.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.BottomNavigationBar
import com.example.wallet.ui.components.EmptyView
import com.example.wallet.ui.components.ErrorView
import com.example.wallet.ui.components.LoadingView
import com.example.wallet.ui.components.PurchaseItem
import com.example.wallet.ui.components.TopBar
import com.example.wallet.viewmodel.PurchasesViewModel

@Composable
fun PurchasesScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    viewModel: PurchasesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val selectedCardId by viewModel.selectedCardId.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopBar(title = "Compras") },
        bottomBar = { BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (cards.isEmpty()) {
                EmptyView("Crie um cartão para visualizar compras.")
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Seletor de cartão (chips)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(cards, key = { it.id }) { card ->
                            FilterChip(
                                selected = card.id == selectedCardId,
                                onClick = { viewModel.selectCard(card.id) },
                                label = { Text("${card.name} •••• ${card.lastDigits}") },
                                colors = FilterChipDefaults.filterChipColors()
                            )
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            state.isLoading -> LoadingView()
                            state.error != null -> ErrorView(message = state.error!!, onRetry = viewModel::load)
                            state.data.isNullOrEmpty() -> EmptyView("Nenhuma compra neste cartão.")
                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(state.data!!, key = { it.id }) { purchase ->
                                        PurchaseItem(purchase = purchase)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
