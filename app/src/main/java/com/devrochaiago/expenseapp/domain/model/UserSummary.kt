package com.devrochaiago.expenseapp.domain.model

data class UserSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0
) {

    val currentBalance: Double
        get() = totalIncome - totalExpense
}