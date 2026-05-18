package com.example.wallet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wallet.ui.screens.*

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Navega para rotas raiz preservando estado e evitando empilhamento.
    val navigateRoot: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Routes.MyCards.route) { saveState = true; inclusive = false }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        // ── Login ──────────────────────────────────────────────
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = { hasCards ->
                    val target = if (hasCards) Routes.MyCards.route else Routes.CreateCard.route
                    navController.navigate(target) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Routes.SignUp.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.ForgotPassword.route)
                }
            )
        }

        // ── Cadastro ───────────────────────────────────────────
        composable(Routes.SignUp.route) {
            SignUpScreen(
                onBackToLogin = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Esqueceu Senha (Solicitação) ────────────────────────
        composable(Routes.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onNavigateToReset = {
                    navController.navigate(Routes.ResetPassword.route)
                }
            )
        }

        // ── Redefinir Senha (Token + Nova Senha) ───────────────
        composable(Routes.ResetPassword.route) {
            ResetPasswordScreen(
                onResetSuccess = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.ForgotPassword.route) { inclusive = true }
                        popUpTo(Routes.ResetPassword.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Criar Cartão ──────────────────────────────────────
        composable(Routes.CreateCard.route) {
            val fromMyCards = navController.previousBackStackEntry
                ?.destination?.route == Routes.MyCards.route

            CreateCardScreen(
                showBackButton = fromMyCards,
                onBack = { navController.popBackStack() },
                onCardCreated = {
                    navController.navigate(Routes.MyCards.route) {
                        popUpTo(Routes.CreateCard.route) { inclusive = true }
                        popUpTo(Routes.MyCards.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Meus Cartões ─────────────────────────────────────
        composable(Routes.MyCards.route) {
            MyCardsScreen(
                currentRoute = currentRoute,
                onNavigate = navigateRoot,
                onCardClick = { cardId ->
                    navController.navigate(Routes.CardDetails.build(cardId))
                },
                onCreateCard = {
                    navController.navigate(Routes.CreateCard.route)
                }
            )
        }

        // ── Compras ───────────────────────────────────────────
        composable(Routes.Purchases.route) {
            PurchasesScreen(currentRoute = currentRoute, onNavigate = navigateRoot)
        }

        // ── Configurações ─────────────────────────────────────
        composable(Routes.Settings.route) {
            SettingsScreen(
                currentRoute = currentRoute,
                onNavigate = navigateRoot,
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Detalhes do Cartão ────────────────────────────────
        composable(
            route = Routes.CardDetails.route,
            arguments = listOf(navArgument(Routes.CardDetails.ARG_CARD_ID) {
                type = NavType.StringType
            })
        ) { entry ->
            val cardId = entry.arguments?.getString(Routes.CardDetails.ARG_CARD_ID).orEmpty()
            CardDetailsScreen(
                cardId = cardId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
