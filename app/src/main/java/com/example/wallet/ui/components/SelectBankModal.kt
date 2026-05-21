package com.example.wallet.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet.data.mock.MockData
import com.example.wallet.model.BankAccount
import com.example.wallet.model.CardModel
import com.example.wallet.utils.ServiceLocator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

private enum class ModalStep {
    SELECT_BANK,
    CONNECT_BANK,
    CONNECTING,
    BANK_CONNECTED_SUCCESS,
    SELECT_BRAND,
    CREATE_CARD_FORM,
    CARD_CREATED_SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBankModal(
    onDismiss: () -> Unit,
    onCardCreated: () -> Unit
) {
    var currentStep by remember { mutableStateOf(ModalStep.SELECT_BANK) }
    var selectedBank by remember { mutableStateOf<BankAccount?>(null) }
    var selectedBrand by remember { mutableStateOf<String?>(null) }

    // Estados do Formulário
    var cardName by remember { mutableStateOf("") }
    var cardLimit by remember { mutableStateOf("") }
    var cardType by remember { mutableStateOf("Virtual") }

    // Carrega dados da fonte de verdade (MockData)
    var connectedBanks by remember {
        mutableStateOf(MockData.banks.filter { it.isConnected })
    }

    var otherBanks by remember {
        mutableStateOf(MockData.banks.filter { !it.isConnected })
    }

    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray.copy(alpha = 0.5f)) }
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "ModalStepTransition"
        ) { step ->
            when (step) {
                ModalStep.SELECT_BANK -> {
                    BankListStep(
                        connectedBanks = connectedBanks,
                        otherBanks = otherBanks,
                        onBankClick = { bank ->
                            selectedBank = bank
                            currentStep = if (bank.isConnected) ModalStep.SELECT_BRAND else ModalStep.CONNECT_BANK
                        },
                        onDismiss = onDismiss
                    )
                }

                ModalStep.CONNECT_BANK -> {
                    ConnectBankStep(
                        bank = selectedBank!!,
                        onConnect = { currentStep = ModalStep.CONNECTING },
                        onBack = { currentStep = ModalStep.SELECT_BANK },
                        onDismiss = onDismiss
                    )
                }

                ModalStep.CONNECTING -> {
                    ConnectingStep {
                        val bank = selectedBank!!
                        val connectedBank = bank.copy(isConnected = true)
                        
                        // Persiste a conexão na fonte de dados global
                        val index = MockData.banks.indexOfFirst { it.name == bank.name }
                        if (index != -1) {
                            MockData.banks[index] = connectedBank
                        }

                        // Atualiza as listas locais para recomposição
                        otherBanks = otherBanks.filter { it.name != bank.name }
                        connectedBanks = connectedBanks + connectedBank
                        selectedBank = connectedBank
                        currentStep = ModalStep.BANK_CONNECTED_SUCCESS
                    }
                }

                ModalStep.BANK_CONNECTED_SUCCESS -> {
                    BankConnectedSuccessStep(
                        bank = selectedBank!!,
                        onCreateCard = { currentStep = ModalStep.SELECT_BRAND },
                        onDismiss = onDismiss
                    )
                }

                ModalStep.SELECT_BRAND -> {
                    SelectBrandStep(
                        bank = selectedBank!!,
                        onBrandSelected = { brand ->
                            selectedBrand = brand
                            currentStep = ModalStep.CREATE_CARD_FORM
                        },
                        onBack = { currentStep = ModalStep.SELECT_BANK },
                        onDismiss = onDismiss
                    )
                }

                ModalStep.CREATE_CARD_FORM -> {
                    CreateCardFormStep(
                        bank = selectedBank!!,
                        brand = selectedBrand ?: "",
                        name = cardName,
                        onNameChange = { cardName = it },
                        type = cardType,
                        onTypeChange = { cardType = it },
                        limit = cardLimit,
                        onLimitChange = { cardLimit = it },
                        onCreate = {
                            val limitValue = cardLimit.toDoubleOrNull() ?: 0.0
                            val finalName = if (cardName.isBlank()) "${selectedBank?.name} $selectedBrand" else cardName
                            val newCard = CardModel(
                                id = UUID.randomUUID().toString(),
                                name = finalName,
                                lastDigits = Random.nextInt(1000, 9999).toString(),
                                limit = limitValue
                            )
                            coroutineScope.launch {
                                ServiceLocator.cardRepository.addCard(newCard)
                                currentStep = ModalStep.CARD_CREATED_SUCCESS
                            }
                        },
                        onDismiss = onDismiss
                    )
                }

                ModalStep.CARD_CREATED_SUCCESS -> {
                    CardCreatedSuccessStep(
                        bankName = selectedBank?.name ?: "",
                        cardName = cardName,
                        onFinish = {
                            onCardCreated()
                            onDismiss()
                        },
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

// --- Componentes das Etapas ---

@Composable
private fun BankListStep(
    connectedBanks: List<BankAccount>,
    otherBanks: List<BankAccount>,
    onBankClick: (BankAccount) -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
        HeaderSection("Selecione o Banco", onDismiss)
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { SectionTitle("Bancos Conectados") }
            items(connectedBanks) { item -> BankItem(item) { onBankClick(item) } }
            item {
                Spacer(Modifier.height(8.dp))
                SectionTitle("Conectar Outro Banco")
            }
            items(otherBanks) { item -> BankItem(item) { onBankClick(item) } }
        }
    }
}

@Composable
private fun ConnectBankStep(
    bank: BankAccount,
    onConnect: () -> Unit,
    onBack: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection("Conectar Banco", onDismiss)
        Spacer(Modifier.height(24.dp))
        BankLogoLarge(bank.color)
        Spacer(Modifier.height(24.dp))
        Text("Conectar ao ${bank.name}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            "Autorize o acesso para gerar cartões virtuais deste banco",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp).padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(24.dp))
        BrandsBadgeRow(bank.networks)
        Spacer(Modifier.height(32.dp))
        InfoRow(Icons.Default.AccountBalance, "Acesso seguro via Open Banking")
        Spacer(Modifier.height(12.dp))
        InfoRow(Icons.Default.Security, "Dados criptografados")
        Spacer(Modifier.height(40.dp))
        PrimaryButtonFull("Conectar e Escolher Bandeira") { onConnect() }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) { Text("Voltar", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun ConnectingStep(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        onFinished()
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF00A3E1), strokeWidth = 3.dp)
        Spacer(Modifier.height(24.dp))
        Text("Conectando ao banco...", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Text("Isso levará apenas alguns segundos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BankConnectedSuccessStep(
    bank: BankAccount,
    onCreateCard: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection("Banco Conectado", onDismiss)
        Spacer(Modifier.height(24.dp))
        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00C853), modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(24.dp))
        Text("Conectado com Sucesso!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            "O ${bank.name} foi conectado a sua conta. Agora você pode criar cartões virtuais deste banco.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp).padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(24.dp))
        BankItem(bank, onClick = {})
        Spacer(Modifier.height(40.dp))
        PrimaryButtonFull("Criar Cartão Virtual") { onCreateCard() }
        Spacer(Modifier.height(12.dp))
        SecondaryButtonFull("Criar Depois") { onDismiss() }
    }
}

@Composable
private fun SelectBrandStep(
    bank: BankAccount,
    onBrandSelected: (String) -> Unit,
    onBack: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection("Escolha a Bandeira", onDismiss)
        Spacer(Modifier.height(24.dp))
        BankLogoLarge(bank.color)
        Spacer(Modifier.height(16.dp))
        Text(bank.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Selecione a bandeira do seu cartão", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.heightIn(max = 280.dp)
        ) {
            items(bank.networks) { network ->
                BrandItemCard(network) { onBrandSelected(network) }
            }
        }
        Spacer(Modifier.height(40.dp))
        TextButton(onClick = onBack) { Text("Voltar", color = MaterialTheme.colorScheme.primary) }
    }
}

@Composable
private fun CreateCardFormStep(
    bank: BankAccount,
    brand: String,
    name: String,
    onNameChange: (String) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    limit: String,
    onLimitChange: (String) -> Unit,
    onCreate: () -> Unit,
    onDismiss: () -> Unit
) {
    val isFormValid = name.isNotBlank() && limit.isNotBlank()

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
        HeaderSection("Criar Cartão Virtual", onDismiss)
        Spacer(Modifier.height(24.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nome do Cartão") },
            placeholder = { Text("Ex: Compras Online") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = type,
            onValueChange = onTypeChange,
            label = { Text("Tipo do Cartão") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = false
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = limit,
            onValueChange = { if (it.all { char -> char.isDigit() }) onLimitChange(it) },
            label = { Text("Limite Mensal") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            prefix = { Text("R$ ", color = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(Modifier.height(40.dp))
        
        Button(
            onClick = onCreate,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = isFormValid,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A3E1))
        ) {
            Text("Criar Cartão", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CardCreatedSuccessStep(
    bankName: String,
    cardName: String,
    onFinish: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection("Cartão Criado", onDismiss)
        Spacer(Modifier.height(24.dp))
        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00C853), modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(24.dp))
        Text("Cartão Criado!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            text = if (cardName.isBlank()) "Seu novo cartão $bankName foi adicionado com sucesso" 
                  else "O cartão \"$cardName\" do $bankName foi adicionado com sucesso",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp).padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(40.dp))
        PrimaryButtonFull("Concluir") { onFinish() }
    }
}

// --- Componentes Atômicos UI ---

@Composable
private fun HeaderSection(title: String, onDismiss: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Fechar", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun BankLogoLarge(color: Color) {
    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(color))
}

@Composable
private fun BrandsBadgeRow(networks: List<String>) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(20.dp)) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Bandeiras disponíveis:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            networks.forEach { NetworkBadge(it) }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFF00A3E1), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun PrimaryButtonFull(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A3E1))) {
        Text(text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SecondaryButtonFull(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFF00A3E1))) {
        Text(text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF00A3E1))
    }
}

@Composable
private fun BrandItemCard(network: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.height(32.dp), contentAlignment = Alignment.Center) { NetworkBadge(network, isLarge = true) }
            Spacer(Modifier.height(12.dp))
            Text(network, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun BankItem(bank: BankAccount, onClick: () -> Unit) {
    Surface(onClick = onClick, color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(bank.color))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(bank.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { bank.networks.forEach { NetworkBadge(it) } }
                }
            }
            if (bank.isConnected) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00C853)))
                    Spacer(Modifier.width(4.dp))
                    Text("Conectado", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00C853))
                }
            } else {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun NetworkBadge(network: String, isLarge: Boolean = false) {
    val scale = if (isLarge) 1.5f else 1f
    val fontSize = if (isLarge) 16.sp else 11.sp
    when (network) {
        "Mastercard" -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size((14 * scale).dp).clip(CircleShape).background(Color(0xFFEB001B)))
                Box(modifier = Modifier.size((14 * scale).dp).offset(x = (-6 * scale).dp).clip(CircleShape).background(Color(0xFFF79E1B).copy(alpha = 0.8f)))
            }
        }
        "Visa" -> { Text("VISA", color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic) }
        "Elo" -> {
            Surface(color = Color(0xFFF9D70B), shape = RoundedCornerShape((4 * scale).dp)) {
                Text("elo", color = Color.Black, fontSize = (fontSize.value * 0.9).sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = (4 * scale).dp, vertical = (1 * scale).dp))
            }
        }
    }
}
