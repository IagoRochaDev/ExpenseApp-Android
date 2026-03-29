package com.devrochaiago.expenseapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: String,
    val type: String,
    val dateMillis: Long,
    val userId: String = "",
    val isSynced: Boolean = false
)