package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.AppTextField
import com.example.wallet.ui.components.PasswordTextField
import com.example.wallet.ui.components.PrimaryButton
import com.example.wallet.viewmodel.ForgotPasswordViewModel

@Composable
fun ResetPasswordScreen(
    onResetSuccess: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val token by viewModel.token.collectAsStateWithLifecycle()
    val newPassword by viewModel.newPassword.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Nova Senha",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Digite o código recebido e sua nova senha",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(40.dp))

        AppTextField(
            value = token,
            onValueChange = viewModel::onTokenChange,
            label = "Código (Token)",
            placeholder = "Cole o código aqui",
            leadingIcon = Icons.Default.VpnKey
        )

        Spacer(Modifier.height(20.dp))

        PasswordTextField(
            value = newPassword,
            onValueChange = viewModel::onNewPasswordChange,
            label = "Nova Senha",
            placeholder = "Mínimo 8 caracteres",
            leadingIcon = Icons.Default.Lock
        )

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = if (state.isLoading) "Redefinindo..." else "Redefinir Senha",
            onClick = { viewModel.confirmPasswordReset(onResetSuccess) },
            loading = state.isLoading
        )

        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
