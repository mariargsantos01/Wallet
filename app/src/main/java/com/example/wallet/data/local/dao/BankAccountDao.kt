package com.example.wallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wallet.data.local.entity.BankAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {

    @Query("SELECT * FROM bank_accounts ORDER BY name ASC")
    fun observeAll(): Flow<List<BankAccountEntity>>

    @Query("SELECT * FROM bank_accounts ORDER BY name ASC")
    suspend fun getAll(): List<BankAccountEntity>

    @Query("SELECT COUNT(*) FROM bank_accounts")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(banks: List<BankAccountEntity>)

    @Query("DELETE FROM bank_accounts")
    suspend fun clear()
}
