package com.devrochaiago.expenseapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devrochaiago.expenseapp.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("""
        SELECT SUM(amount) 
        FROM transactions 
        WHERE type = :transactionType 
        AND dateMillis BETWEEN :startDateMillis AND :endDateMillis
    """)
    fun getTotalAmountByTypeAndDate(
        transactionType: String,
        startDateMillis: Long,
        endDateMillis: Long
    ): Flow<Double?>
}