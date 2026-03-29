package com.devrochaiago.expenseapp.domain.model

import java.util.UUID

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: String,
    val type: TransactionType,
    val dateMillis: Long
)