package com.example.wallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wallet.data.local.entity.PurchaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Query("SELECT * FROM purchases WHERE accountId = :accountId ORDER BY createdAt DESC")
    fun observeByAccount(accountId: Long): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchases WHERE cardId = :cardId ORDER BY createdAt DESC")
    fun observeByCard(cardId: Long): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchases WHERE accountId = :accountId ORDER BY createdAt DESC")
    suspend fun getByAccount(accountId: Long): List<PurchaseEntity>

    @Query("SELECT * FROM purchases ORDER BY createdAt DESC")
    suspend fun getAll(): List<PurchaseEntity>

    @Query("SELECT COUNT(*) FROM purchases")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM purchases WHERE cardId = :cardId")
    suspend fun countByCard(cardId: Long): Int

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM purchases WHERE cardId = :cardId")
    suspend fun sumByCard(cardId: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM purchases WHERE accountId = :accountId")
    suspend fun sumByAccount(accountId: Long): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: PurchaseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(purchases: List<PurchaseEntity>)

    @Query("DELETE FROM purchases WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM purchases WHERE accountId = :accountId")
    suspend fun clearByAccount(accountId: Long)

    @Query("DELETE FROM purchases")
    suspend fun clear()
}
