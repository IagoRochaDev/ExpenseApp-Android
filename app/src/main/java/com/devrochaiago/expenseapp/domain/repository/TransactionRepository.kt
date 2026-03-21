package com.devrochaiago.expenseapp.domain.repository

import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>

    fun getTotalAmountByTypeAndDate(
        type: TransactionType,
        startDateMillis: Long,
        endDateMillis: Long
    ): Flow<Double>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)
}