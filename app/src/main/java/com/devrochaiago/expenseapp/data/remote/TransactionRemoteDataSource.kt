package com.devrochaiago.expenseapp.data.remote

interface TransactionRemoteDataSource {
    suspend fun getUserSummary(userId: String): UserSummaryDto?
    suspend fun updateUserSummary(userId: String, incomeChange: Double, expenseChange: Double)
    suspend fun insertTransaction(transaction: TransactionDto)
    suspend fun updateTransaction(transaction: TransactionDto)
    suspend fun deleteTransaction(userId: String, transactionId: String)
    suspend fun getAllTransactions(userId: String, limit: Long?): List<TransactionDto>
}