package com.example.wallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.wallet.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM cards WHERE accountId = :accountId ORDER BY createdAt ASC")
    fun observeByAccount(accountId: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE accountId = :accountId ORDER BY createdAt ASC")
    suspend fun getByAccount(accountId: String): List<CardEntity>

    @Query("SELECT * FROM cards ORDER BY createdAt ASC")
    suspend fun getAll(): List<CardEntity>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: String): CardEntity?

    @Query("SELECT COUNT(*) FROM cards")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: CardEntity)

    @Update
    suspend fun update(card: CardEntity)

    @Query(
        """
        UPDATE cards SET
            isFavorite = :isFavorite,
            isActive = :isActive,
            dayLimit = :dayLimit,
            nightLimit = :nightLimit
        WHERE id = :id
        """
    )
    suspend fun updateManagement(
        id: String,
        isFavorite: Boolean,
        isActive: Boolean,
        dayLimit: Double,
        nightLimit: Double
    )

    @Query("UPDATE cards SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE cards SET isActive = :isActive WHERE id = :id")
    suspend fun setActive(id: String, isActive: Boolean)

    @Query("UPDATE cards SET dayLimit = :day, nightLimit = :night WHERE id = :id")
    suspend fun updateLimits(id: String, day: Double, night: Double)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM cards")
    suspend fun clear()
}

