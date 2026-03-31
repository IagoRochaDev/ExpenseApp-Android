package com.devrochaiago.expenseapp.data.repository

import com.devrochaiago.expenseapp.data.local.dao.TransactionDao
import com.devrochaiago.expenseapp.data.local.dao.UserSummaryDao
import com.devrochaiago.expenseapp.data.local.entity.TransactionEntity
import com.devrochaiago.expenseapp.data.remote.TransactionRemoteDataSource
import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.domain.model.User
import com.devrochaiago.expenseapp.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionRepositoryImplTest {

    private lateinit var repository: TransactionRepositoryImpl
    private val transactionDao: TransactionDao = mockk()
    private val summaryDao: UserSummaryDao = mockk()
    private val remoteDataSource: TransactionRemoteDataSource = mockk()
    private val authRepository: AuthRepository = mockk()
    private val testScope = TestScope()

    private val testUserId = "test_user_id"
    private val testUser = User(testUserId, "test@email.com")

    @Before
    fun setup() {
        every { authRepository.getCurrentUser() } returns testUser
        repository = TransactionRepositoryImpl(
            transactionDao,
            summaryDao,
            remoteDataSource,
            authRepository,
            testScope
        )
    }

    @Test
    fun `getAllTransactions should map entities to domain models using current user id`() = runTest {
        val entities = listOf(
            TransactionEntity("1", "Title 1", 10.0, "Cat 1", "EXPENSE", 1000L, testUserId),
            TransactionEntity("2", "Title 2", 20.0, "Cat 2", "INCOME", 2000L, testUserId)
        )
        every { transactionDao.getAllTransactions(testUserId) } returns flowOf(entities)

        val result = repository.getAllTransactions().first()

        assertEquals(2, result.size)
        assertEquals("Title 1", result[0].title)
        assertEquals(TransactionType.EXPENSE, result[0].type)
        assertEquals(TransactionType.INCOME, result[1].type)
    }

    @Test
    fun `insertTransaction should call dao, summary dao and remote data source`() = runTest {
        val transaction = Transaction("1", "Title", 10.0, "Cat", TransactionType.EXPENSE, 1000L)
        val expectedEntity = TransactionEntity("1", "Title", 10.0, "Cat", "EXPENSE", 1000L, testUserId, false)

        coEvery { transactionDao.insertTransaction(any()) } returns Unit
        coEvery { summaryDao.getUserSummary(testUserId) } returns null
        coEvery { summaryDao.insertOrUpdateSummary(any()) } returns Unit
        coEvery { remoteDataSource.insertTransaction(any()) } returns Unit
        coEvery { remoteDataSource.updateUserSummary(any(), any(), any()) } returns Unit

        repository.insertTransaction(transaction)
        testScope.advanceUntilIdle()

        coVerify { transactionDao.insertTransaction(expectedEntity) }
        coVerify { summaryDao.insertOrUpdateSummary(match {
            it.userId == testUserId && it.totalExpense == 10.0
        }) }
        coVerify { remoteDataSource.insertTransaction(any()) }
        // Verify that it updates local synced status
        coVerify { transactionDao.insertTransaction(match { it.id == "1" && it.isSynced }) }
    }

    @Test
    fun `deleteTransaction should call dao and remote data source`() = runTest {
        val transaction = Transaction("1", "Title", 10.0, "Cat", TransactionType.EXPENSE, 1000L)
        val expectedEntity = TransactionEntity("1", "Title", 10.0, "Cat", "EXPENSE", 1000L, testUserId, true)

        coEvery { transactionDao.deleteTransaction(any()) } returns Unit
        coEvery { remoteDataSource.deleteTransaction(testUserId, "1") } returns Unit

        repository.deleteTransaction(transaction)
        testScope.advanceUntilIdle()

        coVerify { transactionDao.deleteTransaction(expectedEntity) }
        coVerify { remoteDataSource.deleteTransaction(testUserId, "1") }
    }

    @Test
    fun `getTotalAmountByTypeAndDate should return value from dao with user id`() = runTest {
        val type = TransactionType.EXPENSE
        val start = 0L
        val end = 100L
        every { transactionDao.getTotalAmountByTypeAndDate("EXPENSE", testUserId, start, end) } returns flowOf(50.0)

        val result = repository.getTotalAmountByTypeAndDate(type, start, end).first()

        assertEquals(50.0, result, 0.0)
    }

    @Test
    fun `syncTransactions should process unsynced transactions`() = runTest {
        val unsynced = listOf(
            TransactionEntity("1", "T1", 10.0, "C", "EXPENSE", 1000L, testUserId, false)
        )
        coEvery { transactionDao.getUnsyncedTransactions(testUserId) } returns unsynced
        coEvery { remoteDataSource.insertTransaction(any()) } returns Unit
        coEvery { remoteDataSource.updateUserSummary(any(), any(), any()) } returns Unit
        coEvery { transactionDao.insertTransaction(any()) } returns Unit
        coEvery { remoteDataSource.getUserSummary(testUserId) } returns null
        coEvery { remoteDataSource.getAllTransactions(testUserId, null) } returns emptyList()

        repository.syncTransactions()
        testScope.advanceUntilIdle()

        coVerify { remoteDataSource.insertTransaction(any()) }
        coVerify { transactionDao.insertTransaction(match { it.id == "1" && it.isSynced }) }
    }
}
