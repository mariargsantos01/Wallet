package com.example.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.model.PurchaseCategory
import com.example.wallet.ui.components.AppTextField
import com.example.wallet.ui.components.PrimaryButton
import com.example.wallet.ui.components.TopBar
import com.example.wallet.viewmodel.AddPurchaseViewModel

@Composable
fun AddPurchaseScreen(
    onBack: () -> Unit,
    onPurchaseAdded: () -> Unit,
    viewModel: AddPurchaseViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val title by viewModel.title.collectAsStateWithLifecycle()
    val amount by viewModel.amount.collectAsStateWithLifecycle()
    val date by viewModel.date.collectAsStateWithLifecycle()
    val category by viewModel.category.collectAsStateWithLifecycle()
    val selectedCardId by viewModel.selectedCardId.collectAsStateWithLifecycle()
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TopBar(title = "Nova Compra", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Seletor de cartão
                if (cards.isNotEmpty()) {
                    Text(
                        text = "Cartão",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Chips em linhas de 2
                    val cardChunks = cards.chunked(2)
                    cardChunks.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            row.forEach { card ->
                                FilterChip(
                                    selected = card.id == selectedCardId,
                                    onClick = { viewModel.onCardSelected(card.id) },
                                    label = { Text("${card.name} •${card.lastDigits}") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }

                AppTextField(
                    value = title,
                    onValueChange = viewModel::onTitleChange,
                    label = "Descrição",
                    placeholder = "Ex.: Supermercado, Netflix..."
                )

                AppTextField(
                    value = amount,
                    onValueChange = viewModel::onAmountChange,
                    label = "Valor (R$)",
                    placeholder = "0,00",
                    keyboardType = KeyboardType.Decimal
                )

                AppTextField(
                    value = date,
                    onValueChange = viewModel::onDateChange,
                    label = "Data",
                    placeholder = "dd/mm/aaaa"
                )

                // Categoria
                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Categorias em linhas de 3
                val categoryChunks = PurchaseCategory.entries.chunked(3)
                categoryChunks.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { viewModel.onCategoryChange(cat) },
                                label = { Text(cat.label, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                PrimaryButton(
                    text = if (state.isLoading) "Salvando..." else "Registrar Compra",
                    onClick = { viewModel.submit(onPurchaseAdded) },
                    loading = state.isLoading
                )

                state.error?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
