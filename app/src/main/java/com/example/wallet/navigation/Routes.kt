package com.example.wallet.navigation

/**
 * Rotas de navegação do aplicativo.
 */
sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object SignUp : Routes("signup")
    data object ForgotPassword : Routes("forgot_password")
    data object ResetPassword : Routes("reset_password")
    data object CreateCard : Routes("create_card")
    data object MyCards : Routes("my_cards")
    data object Settings : Routes("settings")
    data object Purchases : Routes("purchases")
    data object EditProfile : Routes("edit_profile")
}
