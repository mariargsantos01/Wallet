package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet.ui.theme.AzulPrimario
import com.example.wallet.ui.theme.Branco
import com.example.wallet.ui.theme.CinzaEscuro
import com.example.wallet.ui.theme.CinzaTexto
import com.example.wallet.ui.theme.FundoPrincipal

@Composable
fun LoginScreen(
    onLoginSuccess: (hasCards: Boolean) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoPrincipal)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ícone de Cadeado
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(AzulPrimario, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Branco
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Títulos
        Text(
            text = "Bem-vindo",
            style = MaterialTheme.typography.headlineMedium,
            color = Branco,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Entre na sua conta para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = CinzaTexto
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Campo Email
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Email",
                color = Branco,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("seu@email.com", color = CinzaTexto) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = CinzaTexto)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CinzaEscuro,
                    unfocusedContainerColor = CinzaEscuro,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Branco,
                    unfocusedTextColor = Branco
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Campo Senha
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Senha",
                color = Branco,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = "********",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = CinzaTexto)
                },
                trailingIcon = {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = CinzaTexto)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CinzaEscuro,
                    unfocusedContainerColor = CinzaEscuro,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Branco,
                    unfocusedTextColor = Branco
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botão Entrar
        Button(
            onClick = { /* Ação futura */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
        ) {
            Text(
                text = "Entrar",
                color = Branco,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Link Esqueci minha senha
        Text(
            text = "Esqueci minha senha",
            color = CinzaTexto,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Footer Cadastre-se
        val signUpText = buildAnnotatedString {
            append("Não tem uma conta? ")
            withStyle(style = SpanStyle(color = AzulPrimario, fontWeight = FontWeight.Bold)) {
                append("Cadastre-se")
            }
        }
        Text(text = signUpText, fontSize = 14.sp, color = CinzaTexto)
    }
}
