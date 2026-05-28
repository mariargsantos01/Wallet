package com.example.wallet.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.BottomNavigationBar
import com.example.wallet.ui.components.CardItem
import com.example.wallet.ui.components.ErrorView
import com.example.wallet.ui.components.LoadingView
import com.example.wallet.ui.components.PrimaryButton
import com.example.wallet.ui.components.PurchaseItem
import com.example.wallet.ui.components.SecondaryButton
import com.example.wallet.ui.components.SectionCard
import com.example.wallet.ui.components.SelectBankModal
import com.example.wallet.ui.components.cardmanagement.CardManagementBottomSheet
import com.example.wallet.ui.components.cardmanagement.DeleteCardFirstDialog
import com.example.wallet.ui.components.cardmanagement.DeleteCardConfirmationDialog
import com.example.wallet.ui.components.TopBar
import com.example.wallet.viewmodel.MyCardsViewModel
import com.example.wallet.viewmodel.PurchasesViewModel
import com.example.wallet.viewmodel.CardManagementViewModel
import kotlinx.coroutines.launch
import com.example.wallet.model.CardModel
import com.example.wallet.utils.ServiceLocator

@Composable
fun MyCardsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onCreateCard: () -> Unit,
    viewModel: MyCardsViewModel = viewModel(),
    purchasesViewModel: PurchasesViewModel = viewModel(),
    cardManagementViewModel: CardManagementViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val allCards = state.data ?: emptyList()
    var filterFavorites by remember { mutableStateOf(false) }
    val cards = if (filterFavorites) allCards.filter { it.isFavorite } else allCards
    var showBankModal by remember { mutableStateOf(false) }
    var showManagementSheet by remember { mutableStateOf(false) }
    var managementCard by remember { mutableStateOf<CardModel?>(null) }
    var pendingSelectCardId by remember { mutableStateOf<Long?>(null) }
    val purchasesState by purchasesViewModel.uiState.collectAsStateWithLifecycle()
    val purchases = purchasesState.data ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { cards.size })
    val cmState by cardManagementViewModel.uiState.collectAsStateWithLifecycle()

    // Dados do usuário logado
    val displayName by ServiceLocator.sessionManager.displayName.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Olá, ${displayName ?: "Usuário"}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (allCards.isNotEmpty()) {
                        IconButton(
                            onClick = { filterFavorites = !filterFavorites },
                            modifier = Modifier
                                .border(
                                    width = 1.5.dp,
                                    color = if (filterFavorites) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .size(40.dp)
                        ) {
                            Icon(
                                if (filterFavorites) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Filtrar favoritos",
                                tint = if (filterFavorites) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = { BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .animateContentSize()
            ) {
                when {
                    state.isLoading -> LoadingView()
                    state.error != null -> ErrorView(message = state.error!!, onRetry = viewModel::load)
                    cards.isEmpty() -> {
                        // Estado vazio — visual clean com call-to-action
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                modifier = Modifier.size(80.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = MaterialTheme.shapes.large,
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.CreditCard,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.height(32.dp))
                            Text(
                                text = "Adicione seu primeiro cartão",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "Cadastre seus cartões para ter acesso rápido e seguro às informações",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(48.dp))
                            // Card tracejado — área de toque para adicionar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clickable { showBankModal = true },
                                contentAlignment = Alignment.Center
                            ) {
                                val borderColor = MaterialTheme.colorScheme.outlineVariant
                                val cornerRadius = 20.dp
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val stroke = Stroke(
                                        width = 2f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                                    )
                                    drawRoundRect(
                                        color = borderColor,
                                        style = stroke,
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        modifier = Modifier.size(52.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        text = "Adicionar cartão",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        // Cards existentes — pager + ações + últimas compras
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${cards.size} cartão(ões) cadastrados",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(bottom = 16.dp)
                            )

                            // Pager com controles
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                                    enabled = pagerState.currentPage > 0
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = null,
                                        tint = if (pagerState.currentPage > 0) MaterialTheme.colorScheme.onSurface
                                               else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                }
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.weight(1f),
                                    pageSpacing = 16.dp
                                ) { page ->
                                    CardItem(card = cards[page], onClick = {})
                                }
                                IconButton(
                                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                                    enabled = pagerState.currentPage < cards.size - 1
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = if (pagerState.currentPage < cards.size - 1) MaterialTheme.colorScheme.onSurface
                                               else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                }
                            }

                            // Indicador de página
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "${pagerState.currentPage + 1} de ${cards.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(24.dp))

                            // Botões de ação
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SecondaryButton(
                                    text = "Gerenciar",
                                    onClick = {
                                        if (cards.isNotEmpty()) {
                                            val card = cards.getOrNull(pagerState.currentPage) ?: cards.first()
                                            managementCard = card
                                            showManagementSheet = true
                                            cardManagementViewModel.openManagement(card)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                PrimaryButton(
                                    text = "+ Novo Cartão",
                                    onClick = { showBankModal = true },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(Modifier.height(32.dp))

                            // Seção de últimas compras
                            Text(
                                text = "ÚLTIMAS COMPRAS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(bottom = 16.dp)
                            )

                            LaunchedEffect(cards) {
                                pendingSelectCardId?.let { id ->
                                    val idx = cards.indexOfFirst { it.id == id }
                                    if (idx >= 0) {
                                        scope.launch { pagerState.animateScrollToPage(idx) }
                                        purchasesViewModel.selectCard(id)
                                        pendingSelectCardId = null
                                    }
                                }
                            }
                            LaunchedEffect(pagerState.currentPage, cards) {
                                if (cards.isNotEmpty() && pagerState.currentPage in cards.indices) {
                                    purchasesViewModel.selectCard(cards[pagerState.currentPage].id)
                                }
                            }

                            when {
                                purchasesState.isLoading -> {
                                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                purchasesState.error != null -> ErrorView(
                                    message = purchasesState.error!!,
                                    onRetry = purchasesViewModel::load
                                )
                                purchases.isEmpty() -> {
                                    Text(
                                        text = "Nenhuma compra registrada para este cartão",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 24.dp)
                                    )
                                }
                                else -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        purchases.take(5).forEach { purchase ->
                                            PurchaseItem(purchase = purchase)
                                        }
                                        if (purchases.size > 5) {
                                            Text(
                                                text = "Ver todas as ${purchases.size} compras",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }
            }
        }

        // Overlays
        if (showManagementSheet && managementCard != null) {
            CardManagementBottomSheet(
                card = managementCard!!,
                state = cmState.managementState,
                onStateChange = { cardManagementViewModel.updateManagementState(it) },
                onDismiss = {
                    showManagementSheet = false
                    cardManagementViewModel.closeManagement()
                },
                onDeleteCard = { cardManagementViewModel.onDeleteCardClicked() },
                visible = true
            )
        }
        if (showBankModal) {
            SelectBankModal(
                onDismiss = { showBankModal = false },
                onCardCreated = { newCardId ->
                    pendingSelectCardId = newCardId
                    showBankModal = false
                    viewModel.load()
                }
            )
        }

        // Diálogo inicial de exclusão (aviso)
        if (cmState.showDeleteWarningDialog && cmState.selectedCard != null) {
            DeleteCardFirstDialog(
                cardBrand = cmState.selectedCard!!.brand,
                cardLastDigits = cmState.selectedCard!!.lastDigits,
                onContinue = { cardManagementViewModel.onContinueWithDeletion() },
                onCancel = { cardManagementViewModel.dismissDeleteWarning() },
                onDismiss = { cardManagementViewModel.dismissDeleteWarning() }
            )
        }

        // Diálogo final de confirmação de exclusão
        if (cmState.showFinalDeleteDialog) {
            DeleteCardConfirmationDialog(
                onConfirmDelete = {
                    cardManagementViewModel.onConfirmDeletePermanently()
                    showManagementSheet = false
                    viewModel.load()
                },
                onKeepCard = { cardManagementViewModel.dismissFinalDelete() },
                onDismiss = { cardManagementViewModel.dismissFinalDelete() }
            )
        }
    }
}
