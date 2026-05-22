package com.example.wallet.utils

import android.content.Context
import com.example.wallet.data.local.AppDatabase
import com.example.wallet.data.mock.MockData
import com.example.wallet.repository.BankRepository
import com.example.wallet.repository.CardRepository
import com.example.wallet.repository.PurchaseRepository
import com.example.wallet.repository.RoomBankRepository
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
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile private var initialized: Boolean = false

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            database = AppDatabase.getInstance(context.applicationContext)
            session = SessionManager(context.applicationContext)
            initialized = true
            // Seed de bancos disponíveis (apenas na primeira execução).
            ioScope.launch {
                (bankRepository as? RoomBankRepository)?.seedIfEmpty(MockData.banks)
            }
        }
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

    val cardRepository: CardRepository by lazy {
        RoomCardRepository(requireDb().cardDao(), sessionManager)
    }
    val purchaseRepository: PurchaseRepository by lazy {
        RoomPurchaseRepository(requireDb().purchaseDao(), sessionManager)
    }
    val userRepository: UserRepository by lazy {
        RoomUserRepository(requireDb().accountDao(), sessionManager)
    }
    val bankRepository: BankRepository by lazy {
        RoomBankRepository(requireDb().bankAccountDao(), requireDb().bankConnectionDao(), sessionManager)
    }
}
