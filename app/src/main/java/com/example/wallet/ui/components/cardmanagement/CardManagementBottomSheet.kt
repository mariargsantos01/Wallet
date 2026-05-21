package com.example.wallet.ui.components.cardmanagement

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wallet.model.CardModel
import com.example.wallet.ui.theme.WalletTheme

/**
 * Estado do gerenciamento do cartão exposto para state hoisting.
 */
data class CardManagementState(
    val isFavorite: Boolean = false,
    val isActive: Boolean = true,
    val dayLimit: Float = 5000f,
    val nightLimit: Float = 2000f,
    val showData: Boolean = false
)

/**
 * Modal flutuante premium para gerenciamento de cartão.
 *
 * Usa AnimatedVisibility + overlay escuro + Surface com bordas arredondadas
 * para criar efeito de painel flutuante centralizado na tela.
 */
@Composable
fun CardManagementBottomSheet(
    card: CardModel,
    state: CardManagementState,
    onStateChange: (CardManagementState) -> Unit,
    onDismiss: () -> Unit,
    onDeleteCard: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)) + slideInVertically(
            animationSpec = tween(350),
            initialOffsetY = { it / 3 }
        ),
        exit = fadeOut(tween(200)) + slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { it / 3 }
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 720.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* Consume click to prevent dismiss */ },
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 16.dp,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header fixo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "Gerenciar Cartão",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Conteúdo scrollável
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {

                    // 2. Card Preview
                    item {
                        CardPreview(
                            brand = "Visa",
                            maskedNumber = if (state.showData) {
                                "4532 1234 5678 ${card.lastDigits}"
                            } else {
                                "•••• •••• •••• ${card.lastDigits}"
                            },
                            holderName = card.name,
                            expiry = "12/28"
                        )
                    }

                    // 3. Cartão Favorito
                    item {
                        SectionCard {
                            SettingsRow(
                                label = "Cartão favorito",
                                description = "Usar como cartão principal",
                                checked = state.isFavorite,
                                onCheckedChange = { onStateChange(state.copy(isFavorite = it)) },
                                icon = Icons.Default.Star,
                                iconTint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // 4. Dados do Cartão
                    item {
                        SectionCard {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Dados do Cartão",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    TextButton(
                                        onClick = { onStateChange(state.copy(showData = !state.showData)) }
                                    ) {
                                        Icon(
                                            imageVector = if (state.showData) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .padding(end = 0.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = if (state.showData) "Ocultar" else "Mostrar",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                CardDataField(
                                    label = "Número",
                                    value = if (state.showData) "4532 1234 5678 ${card.lastDigits}" else "•••• •••• •••• ${card.lastDigits}"
                                )
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        CardDataField(
                                            label = "Validade",
                                            value = "12/28"
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        CardDataField(
                                            label = "CVV",
                                            value = if (state.showData) "123" else "•••"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. Cartão Ativo
                    item {
                        SectionCard {
                            SettingsRow(
                                label = "Cartão ativo",
                                description = if (state.isActive) "O cartão está habilitado" else "O cartão está desativado",
                                checked = state.isActive,
                                onCheckedChange = { onStateChange(state.copy(isActive = it)) },
                                icon = Icons.Default.Lock,
                                iconTint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // 6. Limite Diurno
                    item {
                        SectionCard {
                            LimitSlider(
                                label = "Limite diurno",
                                value = state.dayLimit,
                                onValueChange = { onStateChange(state.copy(dayLimit = it)) },
                                valueRange = 0f..10000f,
                                icon = Icons.Default.WbSunny,
                                iconTint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // 7. Limite Noturno
                    item {
                        SectionCard {
                            LimitSlider(
                                label = "Limite noturno",
                                value = state.nightLimit,
                                onValueChange = { onStateChange(state.copy(nightLimit = it)) },
                                valueRange = 0f..5000f,
                                icon = Icons.Default.Nightlight,
                                iconTint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // 8. Excluir Cartão
                    item {
                        TextButton(
                            onClick = onDeleteCard,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Excluir Cartão",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Bottom spacing
                    item {
                        Spacer(Modifier.height(8.dp))
                    }
                }
                }
            }
        }
    }
}

/**
 * Card de seção reutilizável para visual "flutuante" dentro do modal.
 */
@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * Campo de dado do cartão (label + valor).
 */
@Composable
private fun CardDataField(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardManagementBottomSheetPreview() {
    WalletTheme {
        var state by remember { mutableStateOf(CardManagementState()) }

        CardManagementBottomSheet(
            card = CardModel(
                id = "1",
                name = "João da Silva",
                lastDigits = "4523",
                limit = 5000.0
            ),
            state = state,
            onStateChange = { state = it },
            onDismiss = {},
            onDeleteCard = {}
        )
    }
}
