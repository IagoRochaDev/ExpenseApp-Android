package com.devrochaiago.expenseapp.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.devrochaiago.expenseapp.data.local.ExpenseDatabase
import com.devrochaiago.expenseapp.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TransactionDaoTest {

    private lateinit var database: ExpenseDatabase
    private lateinit var transactionDao: TransactionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ExpenseDatabase::class.java
        ).allowMainThreadQueries().build()
        transactionDao = database.transactionDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetAllTransactions() = runBlocking {
        val transaction = TransactionEntity(
            id = 1,
            title = "Test Expense",
            amount = 50.0,
            category = "Lazer",
            type = "EXPENSE",
            dateMillis = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(transaction)

        val allTransactions = transactionDao.getAllTransactions().first()
        assertEquals(1, allTransactions.size)
        assertEquals(transaction, allTransactions[0])
    }

    @Test
    fun deleteTransaction() = runBlocking {
        val transaction = TransactionEntity(
            id = 1,
            title = "Test Expense",
            amount = 50.0,
            category = "Lazer",
            type = "EXPENSE",
            dateMillis = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(transaction)
        transactionDao.deleteTransaction(transaction)

        val allTransactions = transactionDao.getAllTransactions().first()
        assertTrue(allTransactions.isEmpty())
    }

    @Test
    fun getTotalAmountByTypeAndDate() = runBlocking {
        val now = System.currentTimeMillis()
        val t1 = TransactionEntity(1, "E1", 10.0, "C", "EXPENSE", now)
        val t2 = TransactionEntity(2, "E2", 20.0, "C", "EXPENSE", now)
        val t3 = TransactionEntity(3, "I1", 100.0, "C", "INCOME", now)

        transactionDao.insertTransaction(t1)
        transactionDao.insertTransaction(t2)
        transactionDao.insertTransaction(t3)

        val totalExpense = transactionDao.getTotalAmountByTypeAndDate(
            "EXPENSE",
            now - 1000,
            now + 1000
        ).first()

        assertEquals(30.0, totalExpense!!, 0.0)
    }
}
