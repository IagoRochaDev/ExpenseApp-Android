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
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class TransactionDaoTest {

    private lateinit var database: ExpenseDatabase
    private lateinit var transactionDao: TransactionDao
    private val testUserId = "test_user_id"

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
            id = UUID.randomUUID().toString(),
            title = "Test Expense",
            amount = 50.0,
            category = "Lazer",
            type = "EXPENSE",
            dateMillis = System.currentTimeMillis(),
            userId = testUserId
        )
        transactionDao.insertTransaction(transaction)

        val allTransactions = transactionDao.getAllTransactions(testUserId).first()
        assertEquals(1, allTransactions.size)
        assertEquals(transaction, allTransactions[0])
    }

    @Test
    fun deleteTransaction() = runBlocking {
        val transaction = TransactionEntity(
            id = UUID.randomUUID().toString(),
            title = "Test Expense",
            amount = 50.0,
            category = "Lazer",
            type = "EXPENSE",
            dateMillis = System.currentTimeMillis(),
            userId = testUserId
        )
        transactionDao.insertTransaction(transaction)
        transactionDao.deleteTransaction(transaction)

        val allTransactions = transactionDao.getAllTransactions(testUserId).first()
        assertTrue(allTransactions.isEmpty())
    }

    @Test
    fun getTotalAmountByTypeAndDate() = runBlocking {
        val now = System.currentTimeMillis()
        val t1 = TransactionEntity(UUID.randomUUID().toString(), "E1", 10.0, "C", "EXPENSE", now, testUserId)
        val t2 = TransactionEntity(UUID.randomUUID().toString(), "E2", 20.0, "C", "EXPENSE", now, testUserId)
        val t3 = TransactionEntity(UUID.randomUUID().toString(), "I1", 100.0, "C", "INCOME", now, testUserId)
        
        // Transaction for different user to ensure filtering works
        val t4 = TransactionEntity(UUID.randomUUID().toString(), "E3", 50.0, "C", "EXPENSE", now, "other_user")

        transactionDao.insertTransaction(t1)
        transactionDao.insertTransaction(t2)
        transactionDao.insertTransaction(t3)
        transactionDao.insertTransaction(t4)

        val totalExpense = transactionDao.getTotalAmountByTypeAndDate(
            "EXPENSE",
            testUserId,
            now - 1000,
            now + 1000
        ).first()

        assertEquals(30.0, totalExpense!!, 0.0)
    }

    @Test
    fun getUnsyncedTransactions() = runBlocking {
        val t1 = TransactionEntity(
            id = UUID.randomUUID().toString(),
            title = "E1",
            amount = 10.0,
            category = "C",
            type = "EXPENSE",
            dateMillis = System.currentTimeMillis(),
            userId = testUserId,
            isSynced = false
        )
        val t2 = TransactionEntity(
            id = UUID.randomUUID().toString(),
            title = "E2",
            amount = 20.0,
            category = "C",
            type = "EXPENSE",
            dateMillis = System.currentTimeMillis(),
            userId = testUserId,
            isSynced = true
        )

        transactionDao.insertTransaction(t1)
        transactionDao.insertTransaction(t2)

        val unsynced = transactionDao.getUnsyncedTransactions(testUserId)
        assertEquals(1, unsynced.size)
        assertEquals(t1.id, unsynced[0].id)
    }
}
