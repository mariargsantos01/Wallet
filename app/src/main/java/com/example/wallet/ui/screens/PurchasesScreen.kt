package com.example.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.AppTextField
import com.example.wallet.ui.components.BottomNavigationBar
import com.example.wallet.ui.components.DailySpendingChart
import com.example.wallet.ui.components.EmptyView
import com.example.wallet.ui.components.ErrorView
import com.example.wallet.ui.components.LoadingView
import com.example.wallet.ui.components.MonthlyCategoryChart
import com.example.wallet.ui.components.PurchaseItem
import com.example.wallet.ui.components.SectionCard
import com.example.wallet.ui.components.SpendingDonutChart
import com.example.wallet.ui.components.TopBar
import com.example.wallet.utils.Formatters
import com.example.wallet.viewmodel.PurchasesViewModel

enum class SortMode { DATE, AMOUNT_ASC, AMOUNT_DESC }

@Composable
fun PurchasesScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onAddPurchase: () -> Unit = {},
    viewModel: PurchasesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val selectedCardId by viewModel.selectedCardId.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var sortMode by remember { mutableStateOf(SortMode.DATE) }
    var showSearch by remember { mutableStateOf(false) }

    val purchases = state.data ?: emptyList()
    val filteredPurchases = remember(purchases, searchQuery, sortMode) {
        var result = if (searchQuery.isBlank()) purchases
                     else purchases.filter { it.title.contains(searchQuery, ignoreCase = true) }
        when (sortMode) {
            SortMode.DATE -> result // já ordenado por data (default do dao)
            SortMode.AMOUNT_ASC -> result.sortedBy { it.amount }
            SortMode.AMOUNT_DESC -> result.sortedByDescending { it.amount }
        }
    }
    val totalSpent = filteredPurchases.sumOf { it.amount }

    Scaffold(
        topBar = { TopBar(title = "Compras") },
        bottomBar = { BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        floatingActionButton = {
            if (cards.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onAddPurchase,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar compra")
                }
            }
        },
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
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        items(cards, key = { it.id }) { card ->
                            FilterChip(
                                selected = card.id == selectedCardId,
                                onClick = { viewModel.selectCard(card.id) },
                                label = {
                                    Text(
                                        "${card.name} •••• ${card.lastDigits}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (card.id == selectedCardId) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                shape = MaterialTheme.shapes.small,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    // Barra de busca + ordenação
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (showSearch) {
                            AppTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = "Buscar compra...",
                                leadingIcon = Icons.Default.Search,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                        IconButton(onClick = { showSearch = !showSearch; if (!showSearch) searchQuery = "" }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = if (showSearch) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {
                            sortMode = when (sortMode) {
                                SortMode.DATE -> SortMode.AMOUNT_DESC
                                SortMode.AMOUNT_DESC -> SortMode.AMOUNT_ASC
                                SortMode.AMOUNT_ASC -> SortMode.DATE
                            }
                        }) {
                            Icon(
                                Icons.Default.SortByAlpha,
                                contentDescription = "Ordenar",
                                tint = if (sortMode != SortMode.DATE) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Total gasto
                    if (filteredPurchases.isNotEmpty()) {
                        SectionCard(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total gasto",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = Formatters.currency(totalSpent),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // Lista
                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            state.isLoading -> LoadingView()
                            state.error != null -> ErrorView(message = state.error!!, onRetry = viewModel::load)
                            filteredPurchases.isEmpty() -> EmptyView(
                                if (searchQuery.isNotBlank()) "Nenhuma compra encontrada para \"$searchQuery\"."
                                else "Nenhuma compra neste cartão."
                            )
                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                                ) {
                                    // Gráficos de gastos
                                    item {
                                        SpendingDonutChart(purchases = filteredPurchases)
                                    }
                                    item {
                                        DailySpendingChart(purchases = filteredPurchases)
                                    }
                                    item {
                                        MonthlyCategoryChart(purchases = filteredPurchases)
                                    }
                                    item {
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = "Extrato",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    items(filteredPurchases, key = { it.id }) { purchase ->
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
