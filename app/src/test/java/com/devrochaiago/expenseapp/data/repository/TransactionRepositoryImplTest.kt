package com.devrochaiago.expenseapp.data.repository

import com.devrochaiago.expenseapp.data.local.dao.TransactionDao
import com.devrochaiago.expenseapp.data.local.entity.TransactionEntity
import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.model.TransactionType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TransactionRepositoryImplTest {

    private lateinit var repository: TransactionRepositoryImpl
    private val dao: TransactionDao = mockk()

    @Before
    fun setup() {
        repository = TransactionRepositoryImpl(dao)
    }

    @Test
    fun `getAllTransactions should map entities to domain models`() = runTest {

        val entities = listOf(
            TransactionEntity(1, "Title 1", 10.0, "Cat 1", "EXPENSE", 1000L),
            TransactionEntity(2, "Title 2", 20.0, "Cat 2", "INCOME", 2000L)
        )
        every { dao.getAllTransactions() } returns flowOf(entities)

        val result = repository.getAllTransactions().first()

        assertEquals(2, result.size)
        assertEquals("Title 1", result[0].title)
        assertEquals(TransactionType.EXPENSE, result[0].type)
        assertEquals(TransactionType.INCOME, result[1].type)
    }

    @Test
    fun `insertTransaction should call dao with entity`() = runTest {

        val transaction = Transaction(1, "Title", 10.0, "Cat", TransactionType.EXPENSE, 1000L)
        val expectedEntity = TransactionEntity(1, "Title", 10.0, "Cat", "EXPENSE", 1000L)
        coEvery { dao.insertTransaction(any()) } returns Unit


        repository.insertTransaction(transaction)


        coVerify { dao.insertTransaction(expectedEntity) }
    }

    @Test
    fun `deleteTransaction should call dao with entity`() = runTest {

        val transaction = Transaction(1, "Title", 10.0, "Cat", TransactionType.EXPENSE, 1000L)
        val expectedEntity = TransactionEntity(1, "Title", 10.0, "Cat", "EXPENSE", 1000L)
        coEvery { dao.deleteTransaction(any()) } returns Unit

        repository.deleteTransaction(transaction)

        coVerify { dao.deleteTransaction(expectedEntity) }
    }

    @Test
    fun `getTotalAmountByTypeAndDate should return value from dao or zero if null`() = runTest {

        val type = TransactionType.EXPENSE
        val start = 0L
        val end = 100L
        every { dao.getTotalAmountByTypeAndDate("EXPENSE", start, end) } returns flowOf(50.0)

        val result = repository.getTotalAmountByTypeAndDate(type, start, end).first()

        assertEquals(50.0, result, 0.0)
    }
}
