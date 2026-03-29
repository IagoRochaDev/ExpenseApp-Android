package com.devrochaiago.expenseapp.domain.repository

import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.domain.model.UserSummary
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>

    fun getTotalAmountByTypeAndDate(
        type: TransactionType,
        startDateMillis: Long,
        endDateMillis: Long
    ): Flow<Double>
    fun getUserSummary(): Flow<UserSummary>
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun syncTransactions()
}