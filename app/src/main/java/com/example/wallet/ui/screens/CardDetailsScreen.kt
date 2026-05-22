package com.example.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.AppCard
import com.example.wallet.ui.components.DestructiveButton
import com.example.wallet.ui.components.ErrorView
import com.example.wallet.ui.components.LoadingView
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
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(cardId) { viewModel.loadCard(cardId) }

    Scaffold(
        topBar = { TopBar(title = "Detalhes do Cartão", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(message = state.error!!)
                state.data != null -> {
                    val card = state.data!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppCard {
                            Column {
                                Text(
                                    text = card.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "•••• •••• •••• ${card.lastDigits}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Limite: ${Formatters.currency(card.limit)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Fatura atual: ${Formatters.currency(card.limit * 0.35)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        SecondaryButton(
                            text = "Alterar limite",
                            onClick = { viewModel.changeLimit(card.dayLimit, card.nightLimit) }
                        )

                        DestructiveButton(
                            text = "Bloquear cartão",
                            onClick = { viewModel.blockCard() }
                        )
                    }
                }
            }
        }
    }
}

