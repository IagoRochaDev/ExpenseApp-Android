package com.devrochaiago.expenseapp.domain.model

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val type: TransactionType,
    val dateMillis: Long
)