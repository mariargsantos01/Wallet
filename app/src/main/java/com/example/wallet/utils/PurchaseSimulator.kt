package com.example.wallet.utils

import com.example.wallet.model.PurchaseCategory
import com.example.wallet.model.PurchaseModel
import com.example.wallet.repository.CardRepository
import com.example.wallet.repository.PurchaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Simulador de compras automáticas.
 *
 * Enquanto estiver ativo, gera compras aleatórias a cada 1-5 segundos
 * para os cartões que estejam ativos e dentro da validade.
 */
class PurchaseSimulator(
    private val cardRepository: CardRepository,
    private val purchaseRepository: PurchaseRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null

    companion object {
        /** Máximo de compras geradas por cartão. */
        private const val MAX_PURCHASES_PER_CARD = 15
    }

    /** Inicia a simulação contínua. */
    fun start() {
        if (job?.isActive == true) return
        job = scope.launch {
            while (isActive) {
                val delayMs = (1000L..5000L).random()
                delay(delayMs)

                try {
                    generatePurchaseForRandomCard()
                } catch (_: Exception) {
                    // Ignora erros individuais para não parar o loop
                }
            }
        }
    }

    /** Para a simulação. */
    fun stop() {
        job?.cancel()
        job = null
    }

    private suspend fun generatePurchaseForRandomCard() {
        val cards = cardRepository.getCards()
        // Filtra apenas cartões ativos e válidos
        val validCards = cards.filter { card -> isCardValid(card) }

        if (validCards.isEmpty()) return

        // Escolhe um cartão aleatório entre os válidos
        val chosen = validCards.random()

        // Re-verifica buscando direto do banco (evita dados stale)
        val freshCard = cardRepository.getCardById(chosen.id) ?: return
        if (!isCardValid(freshCard)) return

        // Limite de 15 compras por cartão
        val purchaseCount = purchaseRepository.countByCard(freshCard.id)
        if (purchaseCount >= MAX_PURCHASES_PER_CARD) return

        val randomPurchases = listOf(
            "Supermercado" to PurchaseCategory.FOOD,
            "Uber" to PurchaseCategory.TRANSPORT,
            "Netflix" to PurchaseCategory.SUBSCRIPTION,
            "Farmácia" to PurchaseCategory.HEALTH,
            "Shopping" to PurchaseCategory.SHOPPING,
            "Restaurante" to PurchaseCategory.FOOD,
            "Conta de Luz" to PurchaseCategory.BILLS,
            "Cinema" to PurchaseCategory.ENTERTAINMENT,
            "iFood" to PurchaseCategory.FOOD,
            "Spotify" to PurchaseCategory.SUBSCRIPTION,
            "Amazon" to PurchaseCategory.SHOPPING,
            "Posto de Gasolina" to PurchaseCategory.TRANSPORT,
            "Padaria" to PurchaseCategory.FOOD,
            "Academia" to PurchaseCategory.HEALTH,
            "Steam" to PurchaseCategory.ENTERTAINMENT
        )

        val (title, category) = randomPurchases.random()
        val amount = ((500..25000).random() / 100.0) // R$5,00 a R$250,00

        // Gera uma data aleatória nos últimos 30 dias
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -(0..30).random())
        val randomDate = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(cal.time)

        val purchase = PurchaseModel(
            title = title,
            amount = amount,
            date = randomDate,
            category = category
        )
        purchaseRepository.addPurchase(purchase, freshCard.id)
    }

    /**
     * Verifica se o cartão está apto a receber compras:
     * - Deve estar ativo (não bloqueado)
     * - Se temporário, não pode ter expirado (>24h)
     * - Validade (expiry) não pode estar vencida
     */
    private fun isCardValid(card: com.example.wallet.model.CardModel): Boolean {
        // Cartão bloqueado/desativado
        if (!card.isActive) return false

        // Temporário expirado (mais de 24h)
        if (card.isTemporary) {
            val elapsed = System.currentTimeMillis() - card.createdAt
            if (elapsed > 24 * 60 * 60 * 1000) return false
        }

        // Validade vencida (MM/yy)
        try {
            val parts = card.expiry.split("/")
            val month = parts[0].toInt()
            val year = 2000 + parts[1].toInt()
            val now = Calendar.getInstance()
            val expiryCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }
            if (now.after(expiryCal)) return false
        } catch (_: Exception) { /* formato inválido, considera válido */ }

        return true
    }
}

