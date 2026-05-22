package com.example.wallet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wallet.data.local.dao.AccountDao
import com.example.wallet.data.local.dao.BankAccountDao
import com.example.wallet.data.local.dao.BankConnectionDao
import com.example.wallet.data.local.dao.CardDao
import com.example.wallet.data.local.dao.PurchaseDao
import com.example.wallet.data.local.entity.AccountEntity
import com.example.wallet.data.local.entity.BankAccountEntity
import com.example.wallet.data.local.entity.BankConnectionEntity
import com.example.wallet.data.local.entity.CardEntity
import com.example.wallet.data.local.entity.PurchaseEntity

/**
 * Banco SQLite (Room) único do aplicativo.
 *
 * Versão 1 — caso novas colunas/entidades sejam adicionadas, incrementar
 * [Database.version] e adicionar `Migration`s ao construtor abaixo.
 */
@Database(
    entities = [
        AccountEntity::class,
        CardEntity::class,
        PurchaseEntity::class,
        BankAccountEntity::class,
        BankConnectionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun cardDao(): CardDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun bankConnectionDao(): BankConnectionDao

    companion object {
        private const val DB_NAME = "wallet.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Para protótipos: estratégia destrutiva.
                    // Substituir por Migrations reais antes de produção.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
