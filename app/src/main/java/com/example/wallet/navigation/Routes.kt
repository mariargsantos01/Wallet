package com.example.wallet.navigation

/**
 * Rotas de navegação do aplicativo.
 *
 * Fluxo principal:
 * Login → (tem cartões?) → MyCards  OU  CreateCard → MyCards
 */
sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object CreateCard : Routes("create_card")
    data object MyCards : Routes("my_cards")
    data object Settings : Routes("settings")
    data object Purchases : Routes("purchases")

    data object CardDetails : Routes("card_details/{cardId}") {
        const val ARG_CARD_ID = "cardId"
        fun build(cardId: String) = "card_details/$cardId"
    }
}
