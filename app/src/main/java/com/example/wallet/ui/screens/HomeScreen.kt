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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.model.PurchaseModel
import com.example.wallet.ui.components.CardItem
import com.example.wallet.ui.components.EmptyView
import com.example.wallet.ui.components.ErrorView
import com.example.wallet.ui.components.LoadingView
import com.example.wallet.ui.components.PrimaryButton
import com.example.wallet.ui.components.SecondaryButton
import com.example.wallet.ui.components.TopBar
import com.example.wallet.ui.components.cardmanagement.CardManagementBottomSheet
import com.example.wallet.ui.components.cardmanagement.DeleteCardConfirmationDialog
import com.example.wallet.ui.components.cardmanagement.DeleteCardFirstDialog
import com.example.wallet.viewmodel.CardManagementEvent
import com.example.wallet.viewmodel.CardManagementViewModel
import com.example.wallet.viewmodel.MyCardsViewModel
import kotlinx.coroutines.launch

@Composable
fun MyCardsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onCardClick: (String) -> Unit,
    onCreateCard: () -> Unit,
    viewModel: MyCardsViewModel = viewModel(),
    cardManagementViewModel: CardManagementViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val managementUiState by cardManagementViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val cards = state.data ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { cards.size })
    var cardWasDeleted by remember { mutableStateOf(false) }

    // Observar eventos do ViewModel
    LaunchedEffect(Unit) {
        cardManagementViewModel.events.collect { event ->
            when (event) {
                is CardManagementEvent.CardDeletedSuccessfully -> {
                    cardWasDeleted = true
                    viewModel.load()
                }
            }
        }
    }

    // Após exclusão, se lista vazia → navegar para criação de cartão
    LaunchedEffect(state.data, cardWasDeleted) {
        val cards = state.data
        if (cardWasDeleted && cards != null && cards.isEmpty() && !state.isLoading) {
            cardWasDeleted = false
            onCreateCard()
        }
    }

    val mockPurchases = remember {
        listOf(
            PurchaseModel("1", "Alimentação", 45.90, "Hoje, 12:30"),
            PurchaseModel("2", "Cinema", 32.00, "Ontem, 18:00"),
            PurchaseModel("3", "Farmácia", 15.50, "10 Out, 10:15"),
            PurchaseModel("4", "Supermercado", 120.00, "08 Out, 14:20")
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopBar(
                    title = "Meus Cartões",
                    actions = {
                        Surface(
                            onClick = { /* favoritar */ },
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Favoritar",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    state.isLoading -> LoadingView()
                    state.error != null -> ErrorView(
                        message = state.error!!,
                        onRetry = viewModel::load
                    )
                    state.data != null -> {
                        val cards = state.data!!
                        if (cards.isEmpty()) {
                            EmptyView(message = "Nenhum cartão encontrado.\nCrie um novo cartão!")
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${cards.size} cartão(ões) cadastrados",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(bottom = 16.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                            }
                                        },
                                        enabled = pagerState.currentPage > 0
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                            contentDescription = "Anterior",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier.weight(1f),
                                        pageSpacing = 16.dp
                                    ) { page ->
                                        CardItem(
                                            card = cards[page],
                                            onClick = { onCardClick(cards[page].id) }
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                            }
                                        },
                                        enabled = pagerState.currentPage < cards.size - 1
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = "Próximo",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = "${pagerState.currentPage + 1} de ${cards.size}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 12.dp)
                                )

                                Spacer(Modifier.height(20.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SecondaryButton(
                                        text = "Gerenciar",
                                        onClick = {
                                            val currentCard = cards.getOrNull(pagerState.currentPage)
                                            if (currentCard != null) {
                                                cardManagementViewModel.openManagement(currentCard)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        PrimaryButton(
                                            text = "+ Novo Cartão",
                                            onClick = onCreateCard
                                        )
                                    }
                                }

                                Spacer(Modifier.height(28.dp))

                                Text(
                                    text = "ÚLTIMAS COMPRAS",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(bottom = 8.dp)
                                )

                                mockPurchases.forEach { purchase ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = purchase.title,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = purchase.date,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "-R$ ${"%.2f".format(purchase.amount)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    HorizontalDivider(
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }

                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Floating Card Management Modal (rendered on top of everything)
        if (managementUiState.isManagementSheetOpen && managementUiState.selectedCard != null) {
            val selectedCard = managementUiState.selectedCard!!

            CardManagementBottomSheet(
                card = selectedCard,
                state = managementUiState.managementState,
                onStateChange = { cardManagementViewModel.updateManagementState(it) },
                onDismiss = { cardManagementViewModel.closeManagement() },
                onDeleteCard = { cardManagementViewModel.onDeleteCardClicked() }
            )

            // Primeiro Dialog: Aviso de exclusão
            if (managementUiState.showDeleteWarningDialog) {
                DeleteCardFirstDialog(
                    cardBrand = "VISA",
                    cardLastDigits = selectedCard.lastDigits,
                    onContinue = { cardManagementViewModel.onContinueWithDeletion() },
                    onCancel = { cardManagementViewModel.dismissDeleteWarning() },
                    onDismiss = { cardManagementViewModel.dismissDeleteWarning() }
                )
            }

            // Segundo Dialog: Confirmação final
            if (managementUiState.showFinalDeleteDialog) {
                DeleteCardConfirmationDialog(
                    onConfirmDelete = { cardManagementViewModel.onConfirmDeletePermanently() },
                    onKeepCard = { cardManagementViewModel.dismissFinalDelete() },
                    onDismiss = { cardManagementViewModel.dismissFinalDelete() }
                )
            }
        }
    }
}
