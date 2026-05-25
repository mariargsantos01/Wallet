package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.AppTextField
import com.example.wallet.ui.components.LinkButton
import com.example.wallet.ui.components.PasswordTextField
import com.example.wallet.ui.components.PrimaryButton
import com.example.wallet.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (hasCards: Boolean) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val username by viewModel.username.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(48.dp))

        // Logo / ícone
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.large
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Bem-vindo",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Entre na sua conta para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(40.dp))

        AppTextField(
            value = username,
            onValueChange = viewModel::onUsernameChange,
            label = "Usuário",
            placeholder = "joao.silva",
            leadingIcon = Icons.Default.Person,
            keyboardType = KeyboardType.Text
        )

        Spacer(Modifier.height(20.dp))

        PasswordTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = "Senha",
            placeholder = "••••••••",
            leadingIcon = Icons.Default.Lock
        )

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = if (state.isLoading) "Entrando..." else "Entrar",
            onClick = { viewModel.login(onLoginSuccess) },
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

        Spacer(Modifier.height(12.dp))

        LinkButton(
            text = "Esqueci minha senha",
            onClick = onNavigateToForgotPassword,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        val signUpText = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                append("Não tem uma conta? ")
            }
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append("Cadastre-se")
            }
        }
        Text(
            text = signUpText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable { onNavigateToSignUp() }
        )

        Spacer(Modifier.height(48.dp))
    }
}
