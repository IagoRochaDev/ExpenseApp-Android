package com.devrochaiago.expenseapp.data.remote

data class TransactionDto(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val type: String = "",
    val dateMillis: Long = 0L
)