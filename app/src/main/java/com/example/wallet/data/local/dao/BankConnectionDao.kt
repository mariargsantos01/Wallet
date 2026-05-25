package com.example.wallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wallet.data.local.entity.BankConnectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankConnectionDao {

    @Query("SELECT bankName FROM bank_connections WHERE accountId = :accountId")
    fun observeConnectedBankNames(accountId: Long): Flow<List<String>>

    @Query("SELECT bankName FROM bank_connections WHERE accountId = :accountId")
    suspend fun getConnectedBankNames(accountId: Long): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun connect(connection: BankConnectionEntity)

    @Query("DELETE FROM bank_connections WHERE accountId = :accountId AND bankName = :bankName")
    suspend fun disconnect(accountId: Long, bankName: String)

    @Query("DELETE FROM bank_connections WHERE accountId = :accountId")
    suspend fun disconnectAll(accountId: Long)
}
