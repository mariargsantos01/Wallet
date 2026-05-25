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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.AppCard
import com.example.wallet.ui.components.AppTextField
import com.example.wallet.ui.components.DestructiveButton
import com.example.wallet.ui.components.ErrorView
import com.example.wallet.ui.components.LoadingView
import com.example.wallet.ui.components.PrimaryButton
import com.example.wallet.ui.components.SecondaryButton
import com.example.wallet.ui.components.TopBar
import com.example.wallet.utils.Formatters
import com.example.wallet.viewmodel.CardDetailsViewModel

@Composable
fun CardDetailsScreen(
    cardId: Long,
    onBack: () -> Unit,
    viewModel: CardDetailsViewModel = viewModel()
) {
    val detailState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(cardId) { viewModel.loadCard(cardId) }

    LaunchedEffect(detailState.snackMessage) {
        detailState.snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnack()
        }
    }

    Scaffold(
        topBar = { TopBar(title = "Detalhes do Cartão", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                detailState.isLoading -> LoadingView()
                detailState.error != null -> ErrorView(message = detailState.error!!)
                detailState.card != null -> {
                    val card = detailState.card!!
                    val usedPercent = if (card.limit > 0) (detailState.totalSpent / card.limit).coerceIn(0.0, 1.0) else 0.0

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppCard {
                            Column {
                                Text(
                                    text = card.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "•••• •••• •••• ${card.lastDigits}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Validade: ${card.expiry} • ${card.brand}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (!card.isActive) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "BLOQUEADO",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        // Resumo financeiro
                        AppCard {
                            Column {
                                Text(
                                    text = "Fatura Atual",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = Formatters.currency(detailState.totalSpent),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "de ${Formatters.currency(card.limit)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                LinearProgressIndicator(
                                    progress = { usedPercent.toFloat() },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = if (usedPercent > 0.8) MaterialTheme.colorScheme.error
                                            else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Limite diurno: ${Formatters.currency(card.dayLimit)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Noturno: ${Formatters.currency(card.nightLimit)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        PrimaryButton(
                            text = if (card.isFavorite) "Remover dos Favoritos" else "Favoritar Cartão",
                            onClick = { viewModel.toggleFavorite() }
                        )

                        SecondaryButton(
                            text = "Alterar Limites",
                            onClick = { viewModel.showLimitDialog() }
                        )

                        DestructiveButton(
                            text = if (card.isActive) "Bloquear Cartão" else "Desbloquear Cartão",
                            onClick = { viewModel.blockCard() }
                        )
                    }
                }
            }

            // Dialog para alterar limites
            if (detailState.showLimitDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissLimitDialog() },
                    title = { Text("Alterar Limites") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            AppTextField(
                                value = detailState.newDayLimit,
                                onValueChange = viewModel::onDayLimitChange,
                                label = "Limite Diurno (R$)",
                                keyboardType = KeyboardType.Number
                            )
                            AppTextField(
                                value = detailState.newNightLimit,
                                onValueChange = viewModel::onNightLimitChange,
                                label = "Limite Noturno (R$)",
                                keyboardType = KeyboardType.Number
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.confirmLimitChange() }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.dismissLimitDialog() }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
