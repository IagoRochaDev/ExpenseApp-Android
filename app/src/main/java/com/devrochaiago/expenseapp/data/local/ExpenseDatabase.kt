package com.devrochaiago.expenseapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devrochaiago.expenseapp.data.local.dao.TransactionDao
import com.devrochaiago.expenseapp.data.local.dao.UserSummaryDao
import com.devrochaiago.expenseapp.data.local.entity.TransactionEntity
import com.devrochaiago.expenseapp.data.local.entity.UserSummaryEntity

@Database(
    entities = [TransactionEntity::class, UserSummaryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun userSummaryDao(): UserSummaryDao
}