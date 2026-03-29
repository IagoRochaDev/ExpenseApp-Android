package com.devrochaiago.expenseapp.data.repository

import com.devrochaiago.expenseapp.data.local.dao.TransactionDao
import com.devrochaiago.expenseapp.data.local.dao.UserSummaryDao
import com.devrochaiago.expenseapp.data.local.entity.TransactionEntity
import com.devrochaiago.expenseapp.data.local.entity.UserSummaryEntity
import com.devrochaiago.expenseapp.data.remote.TransactionDto
import com.devrochaiago.expenseapp.data.remote.TransactionRemoteDataSource
import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.domain.model.UserSummary
import com.devrochaiago.expenseapp.domain.repository.AuthRepository
import com.devrochaiago.expenseapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val summaryDao: UserSummaryDao,
    private val remoteDataSource: TransactionRemoteDataSource,
    private val authRepository: AuthRepository
) : TransactionRepository {

    private val currentUserId: String
        get() = authRepository.getCurrentUser()?.id
            ?: throw IllegalStateException("Usuário não logado")

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions(currentUserId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTotalAmountByTypeAndDate(
        type: TransactionType,
        startDateMillis: Long,
        endDateMillis: Long
    ): Flow<Double> {
        return transactionDao.getTotalAmountByTypeAndDate(currentUserId, type.name, startDateMillis, endDateMillis)
            .map { it ?: 0.0 }
    }

    override fun getUserSummary(): Flow<UserSummary> {
        return summaryDao.getUserSummaryFlow(currentUserId).map { entity ->
            // Se não tiver nada no Room ainda, retorna zerado
            if (entity == null) {
                UserSummary(0.0, 0.0)
            } else {
                UserSummary(entity.totalIncome, entity.totalExpense)
            }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        val userId = currentUserId

        // 1. Salva a transação localmente
        val entity = transaction.toEntity(userId = userId, isSynced = false)
        transactionDao.insertTransaction(entity)

        // 2. Calcula a mudança para os metadados (Se for receita, mexe no income; se for despesa, mexe no expense)
        val incomeChange = if (transaction.type == TransactionType.INCOME) transaction.amount else 0.0
        val expenseChange = if (transaction.type == TransactionType.EXPENSE) transaction.amount else 0.0

        // 3. Atualiza os Metadados Locais (Room)
        val currentSummary = summaryDao.getUserSummary(userId) ?: UserSummaryEntity(userId = userId)
        val updatedSummary = currentSummary.copy(
            totalIncome = currentSummary.totalIncome + incomeChange,
            totalExpense = currentSummary.totalExpense + expenseChange,
            lastUpdated = System.currentTimeMillis()
        )
        summaryDao.insertOrUpdateSummary(updatedSummary)

        // 4. Tenta enviar para o Firestore (Transação + Metadados)
        try {
            // Salva a transação na nuvem
            remoteDataSource.insertTransaction(entity.toDto())

            // Incrementa o saldo na nuvem magicamente
            remoteDataSource.updateUserSummary(userId, incomeChange, expenseChange)

            // Marca a transação como sincronizada no Room
            transactionDao.insertTransaction(entity.copy(isSynced = true))
        } catch (e: Exception) {
            // Sem internet? Tudo bem, o saldo e a transação já estão no Room!
            e.printStackTrace()
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        val userId = currentUserId

        transactionDao.deleteTransaction(transaction.toEntity(userId = userId, isSynced = true))

        try {
            remoteDataSource.deleteTransaction(userId, transaction.id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun TransactionEntity.toDomainModel(): Transaction {
    return Transaction(
        id = id,
        title = title,
        amount = amount,
        category = category,
        type = TransactionType.valueOf(type),
        dateMillis = dateMillis
    )
}

fun Transaction.toEntity(userId: String, isSynced: Boolean): TransactionEntity {
    return TransactionEntity(
        id = id,
        title = title,
        amount = amount,
        category = category,
        type = type.name,
        dateMillis = dateMillis,
        userId = userId,
        isSynced = isSynced
    )
}

fun TransactionEntity.toDto(): TransactionDto {
    return TransactionDto(
        id = id,
        userId = userId,
        title = title,
        amount = amount,
        category = category,
        type = type,
        dateMillis = dateMillis
    )
}
