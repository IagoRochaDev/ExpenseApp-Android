package com.devrochaiago.expenseapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_summary")
data class UserSummaryEntity(
    @PrimaryKey
    val userId: String,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)