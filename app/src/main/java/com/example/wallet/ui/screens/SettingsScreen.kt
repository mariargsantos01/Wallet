package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.BottomNavigationBar
import com.example.wallet.ui.components.SectionCard
import com.example.wallet.ui.components.TopBar
import com.example.wallet.utils.ServiceLocator
import com.example.wallet.utils.ThemeMode
import com.example.wallet.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Dados do usuário logado
    val displayName by ServiceLocator.sessionManager.displayName.collectAsStateWithLifecycle()
    val username by ServiceLocator.sessionManager.username.collectAsStateWithLifecycle()
    val userEmail by ServiceLocator.sessionManager.email.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopBar(title = "Ajustes") },
        bottomBar = { BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val itemColors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    headlineColor = MaterialTheme.colorScheme.onBackground,
                    supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    leadingIconColor = MaterialTheme.colorScheme.primary
                )

                // ── Perfil do Usuário ────────────────────────────────
                SectionCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditProfile() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Foto do perfil",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.padding(start = 16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayName ?: "Usuário",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (username != null) {
                                Text(
                                    text = "@$username",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (userEmail != null && userEmail != username) {
                                Text(
                                    text = userEmail!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar perfil",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Seção: Tema ──────────────────────────────────────
                SectionCard {
                    ListItem(
                        headlineContent = {
                            Text("Tema", fontWeight = FontWeight.Medium)
                        },
                        supportingContent = {
                            Text(
                                when (themeMode) {
                                    ThemeMode.LIGHT -> "Claro"
                                    ThemeMode.DARK -> "Escuro"
                                    ThemeMode.SYSTEM -> "Sistema"
                                }
                            )
                        },
                        leadingContent = { Icon(Icons.Default.Palette, contentDescription = "Tema") },
                        colors = itemColors
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemeChip(
                            label = "Claro",
                            icon = Icons.Default.LightMode,
                            selected = themeMode == ThemeMode.LIGHT,
                            onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeChip(
                            label = "Escuro",
                            icon = Icons.Default.DarkMode,
                            selected = themeMode == ThemeMode.DARK,
                            onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeChip(
                            label = "Sistema",
                            icon = Icons.Default.PhoneAndroid,
                            selected = themeMode == ThemeMode.SYSTEM,
                            onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Seção: Segurança ─────────────────────────────────
                SectionCard {
                    ListItem(
                        headlineContent = {
                            Text("Segurança", fontWeight = FontWeight.Medium)
                        },
                        supportingContent = { Text("Senha, biometria") },
                        leadingContent = { Icon(Icons.Default.Lock, contentDescription = "Segurança") },
                        colors = itemColors
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ── Excluir Conta ────────────────────────────────────
                SectionCard {
                    ListItem(
                        headlineContent = {
                            Text("Excluir Conta", fontWeight = FontWeight.Medium)
                        },
                        supportingContent = { Text("Remove todos os dados permanentemente") },
                        leadingContent = {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir conta")
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                            headlineColor = MaterialTheme.colorScheme.error,
                            supportingColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            leadingIconColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.clickable { showDeleteDialog = true }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ── Sair ─────────────────────────────────────────────
                SectionCard {
                    ListItem(
                        headlineContent = {
                            Text("Sair", fontWeight = FontWeight.Medium)
                        },
                        leadingContent = {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair")
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                            headlineColor = MaterialTheme.colorScheme.error,
                            leadingIconColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.clickable { viewModel.logout(onLogout) }
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        // Dialog de exclusão de conta
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Excluir Conta") },
                text = { Text("Tem certeza? Todos os seus cartões, compras e dados serão removidos permanentemente. Esta ação não pode ser desfeita.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAccount(onLogout)
                    }) {
                        Text("Excluir", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun ThemeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        shape = MaterialTheme.shapes.small,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.primary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
    )
}
