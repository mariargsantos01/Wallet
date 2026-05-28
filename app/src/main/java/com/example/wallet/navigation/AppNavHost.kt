package com.example.wallet.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wallet.ui.screens.*

// Ordem das abas do menu inferior para determinar direção do slide
private val bottomNavOrder = listOf(
    Routes.MyCards.route,
    Routes.Purchases.route,
    Routes.Settings.route
)

@Composable
fun AppNavHost(
    startDestination: String = Routes.Login.route,
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
        startDestination = startDestination
    ) {

        // ── Login ──────────────────────────────────────────────
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = { hasCards ->
                    navController.navigate(Routes.MyCards.route) {
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

        // ── Esqueceu Senha ────────────────────────────────────
        composable(Routes.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onNavigateToReset = {
                    navController.navigate(Routes.ResetPassword.route)
                }
            )
        }

        // ── Redefinir Senha ───────────────────────────────────
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
        composable(
            Routes.MyCards.route,
            enterTransition = {
                val fromIndex = bottomNavOrder.indexOf(initialState.destination.route)
                val toIndex = bottomNavOrder.indexOf(Routes.MyCards.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideIntoContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeIn(tween(300))
            },
            exitTransition = {
                val fromIndex = bottomNavOrder.indexOf(Routes.MyCards.route)
                val toIndex = bottomNavOrder.indexOf(targetState.destination.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideOutOfContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeOut(tween(300))
            },
            popEnterTransition = {
                val fromIndex = bottomNavOrder.indexOf(initialState.destination.route)
                val toIndex = bottomNavOrder.indexOf(Routes.MyCards.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideIntoContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeIn(tween(300))
            },
            popExitTransition = {
                val fromIndex = bottomNavOrder.indexOf(Routes.MyCards.route)
                val toIndex = bottomNavOrder.indexOf(targetState.destination.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideOutOfContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeOut(tween(300))
            }
        ) {
            MyCardsScreen(
                currentRoute = currentRoute,
                onNavigate = navigateRoot,
                onCreateCard = {
                    navController.navigate(Routes.CreateCard.route)
                }
            )
        }

        // ── Compras ───────────────────────────────────────────
        composable(
            Routes.Purchases.route,
            enterTransition = {
                val fromIndex = bottomNavOrder.indexOf(initialState.destination.route)
                val toIndex = bottomNavOrder.indexOf(Routes.Purchases.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideIntoContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeIn(tween(300))
            },
            exitTransition = {
                val fromIndex = bottomNavOrder.indexOf(Routes.Purchases.route)
                val toIndex = bottomNavOrder.indexOf(targetState.destination.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideOutOfContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeOut(tween(300))
            },
            popEnterTransition = {
                val fromIndex = bottomNavOrder.indexOf(initialState.destination.route)
                val toIndex = bottomNavOrder.indexOf(Routes.Purchases.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideIntoContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeIn(tween(300))
            },
            popExitTransition = {
                val fromIndex = bottomNavOrder.indexOf(Routes.Purchases.route)
                val toIndex = bottomNavOrder.indexOf(targetState.destination.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideOutOfContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeOut(tween(300))
            }
        ) {
            PurchasesScreen(
                currentRoute = currentRoute,
                onNavigate = navigateRoot
            )
        }

        // ── Configurações ─────────────────────────────────────
        composable(
            Routes.Settings.route,
            enterTransition = {
                val fromIndex = bottomNavOrder.indexOf(initialState.destination.route)
                val toIndex = bottomNavOrder.indexOf(Routes.Settings.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideIntoContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeIn(tween(300))
            },
            exitTransition = {
                val fromIndex = bottomNavOrder.indexOf(Routes.Settings.route)
                val toIndex = bottomNavOrder.indexOf(targetState.destination.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideOutOfContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeOut(tween(300))
            },
            popEnterTransition = {
                val fromIndex = bottomNavOrder.indexOf(initialState.destination.route)
                val toIndex = bottomNavOrder.indexOf(Routes.Settings.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideIntoContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeIn(tween(300))
            },
            popExitTransition = {
                val fromIndex = bottomNavOrder.indexOf(Routes.Settings.route)
                val toIndex = bottomNavOrder.indexOf(targetState.destination.route)
                if (fromIndex >= 0 && toIndex >= 0) {
                    slideOutOfContainer(
                        if (toIndex > fromIndex) AnimatedContentTransitionScope.SlideDirection.Left
                        else AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                } else fadeOut(tween(300))
            }
        ) {
            SettingsScreen(
                currentRoute = currentRoute,
                onNavigate = navigateRoot,
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEditProfile = {
                    navController.navigate(Routes.EditProfile.route)
                },
                onResetPassword = {
                    navController.navigate(Routes.ForgotPassword.route)
                }
            )
        }



        // ── Editar Perfil ─────────────────────────────────────
        composable(Routes.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
