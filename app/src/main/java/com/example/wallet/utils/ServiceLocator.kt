package com.example.wallet.utils

import android.content.Context
import com.example.wallet.data.local.AppDatabase
import com.example.wallet.data.local.entity.PurchaseEntity
import com.example.wallet.data.mock.MockData
import com.example.wallet.repository.BankRepository
import com.example.wallet.repository.CardRepository
import com.example.wallet.repository.PurchaseRepository
import com.example.wallet.repository.RoomBankRepository
import com.example.wallet.data.local.CardPreferencesManager
import com.example.wallet.repository.RoomCardRepository
import com.example.wallet.repository.RoomPurchaseRepository
import com.example.wallet.repository.RoomUserRepository
import com.example.wallet.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Service Locator simples.
 *
 * Após [init], expõe os repositories reais (Room/SQLite).
 * Substituir por Hilt/Koin em projetos maiores.
 */
object ServiceLocator {

    private lateinit var database: AppDatabase
    private lateinit var session: SessionManager
    private lateinit var cardPrefs: CardPreferencesManager
    private lateinit var themePrefs: ThemePreferences
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile private var initialized: Boolean = false

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            database = AppDatabase.getInstance(context.applicationContext)
            session = SessionManager(context.applicationContext)
            cardPrefs = CardPreferencesManager(context.applicationContext)
            themePrefs = ThemePreferences(context.applicationContext)
            initialized = true
            // Seed de bancos disponíveis (apenas na primeira execução).
            ioScope.launch {
                (bankRepository as? RoomBankRepository)?.seedIfEmpty(MockData.banks)
                seedPurchasesIfNeeded()
            }
        }
    }

    /**
     * Insere compras de exemplo quando o usuário tem cartões mas nenhuma compra.
     * Executa apenas 1 vez — quando a tabela de compras estiver vazia.
     * Chamado automaticamente no init e também após login/criação de cartão.
     */
    fun trySeedPurchases() {
        ioScope.launch { seedPurchasesIfNeeded() }
    }

    private suspend fun seedPurchasesIfNeeded() {
        val purchaseDao = database.purchaseDao()
        val cardDao = database.cardDao()

        // Se já existem compras, não faz nada
        if (purchaseDao.count() > 0) return

        // Busca o usuário logado
        val accountId = session.getCurrentUserId() ?: return

        // Busca cartões do usuário
        val cards = cardDao.getByAccount(accountId)
        if (cards.isEmpty()) return

        // Distribui compras entre os cartões existentes
        val samplePurchases = listOf(
            Triple("Supermercado Extra", 245.90, "FOOD"),
            Triple("Netflix", 39.90, "SUBSCRIPTION"),
            Triple("Restaurante Japa", 128.50, "FOOD"),
            Triple("Posto Shell", 200.00, "TRANSPORT"),
            Triple("Farmácia Drogasil", 76.20, "HEALTH"),
            Triple("Amazon.com.br", 349.99, "SHOPPING"),
            Triple("Uber", 32.50, "TRANSPORT"),
            Triple("iFood", 67.80, "FOOD"),
            Triple("Spotify", 21.90, "SUBSCRIPTION"),
            Triple("Padaria Pão Quente", 18.50, "FOOD"),
            Triple("Mercado Livre", 159.00, "SHOPPING"),
            Triple("Cinema Cinemark", 52.00, "ENTERTAINMENT"),
            Triple("Academia SmartFit", 99.90, "HEALTH"),
            Triple("Conta de Luz", 185.30, "BILLS"),
            Triple("Pet Shop", 120.00, "SHOPPING")
        )

        val dates = listOf(
            "25/05/2026", "22/05/2026", "20/05/2026", "18/05/2026", "15/05/2026",
            "14/05/2026", "12/05/2026", "10/05/2026", "08/05/2026", "07/05/2026",
            "05/05/2026", "03/05/2026", "01/05/2026", "28/04/2026", "25/04/2026"
        )

        val entities = samplePurchases.mapIndexed { index, (title, amount, category) ->
            val card = cards[index % cards.size]
            PurchaseEntity(
                accountId = accountId,
                cardId = card.id,
                title = title,
                amount = amount,
                date = dates[index],
                category = category,
                createdAt = System.currentTimeMillis() - (index * 86_400_000L)
            )
        }

        purchaseDao.insertAll(entities)
    }

    private fun requireDb(): AppDatabase {
        check(initialized) { "ServiceLocator não inicializado. Chame ServiceLocator.init(context) no Application/Activity." }
        return database
    }

    val sessionManager: SessionManager
        get() {
            check(initialized) { "ServiceLocator não inicializado." }
            return session
        }

    val cardPreferencesManager: CardPreferencesManager
        get() {
            check(initialized) { "ServiceLocator não inicializado." }
            return cardPrefs
        }

    val themePreferences: ThemePreferences
        get() {
            check(initialized) { "ServiceLocator não inicializado." }
            return themePrefs
        }

    val cardRepository: CardRepository by lazy {
        RoomCardRepository(requireDb().cardDao(), sessionManager)
    }
    val purchaseRepository: PurchaseRepository by lazy {
        RoomPurchaseRepository(requireDb().purchaseDao(), sessionManager)
    }
    val userRepository: UserRepository by lazy {
        RoomUserRepository(requireDb().accountDao(), sessionManager, requireDb().cardDao(), requireDb().purchaseDao())
    }
    val bankRepository: BankRepository by lazy {
        RoomBankRepository(requireDb().bankAccountDao(), requireDb().bankConnectionDao(), sessionManager)
    }

    val purchaseSimulator: PurchaseSimulator by lazy {
        PurchaseSimulator(cardRepository, purchaseRepository)
    }

    /** Inicia o simulador de compras automáticas. */
    fun startPurchaseSimulator() {
        purchaseSimulator.start()
    }

    /** Para o simulador de compras (ex: ao fazer logout). */
    fun stopPurchaseSimulator() {
        purchaseSimulator.stop()
    }
}
