package com.devrochaiago.expenseapp.data.repository

import com.devrochaiago.expenseapp.data.local.dao.TransactionDao
import com.devrochaiago.expenseapp.data.local.entity.TransactionEntity
import com.devrochaiago.expenseapp.data.remote.TransactionDto
import com.devrochaiago.expenseapp.data.remote.TransactionRemoteDataSource
import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.domain.repository.AuthRepository
import com.devrochaiago.expenseapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao,
    private val remoteDataSource: TransactionRemoteDataSource,
    private val authRepository: AuthRepository
) : TransactionRepository {

    private val currentUserId: String
        get() = authRepository.getCurrentUser()?.id
            ?: throw IllegalStateException("Tentativa de acessar transações sem usuário logado")

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions(currentUserId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTotalAmountByTypeAndDate(
        type: TransactionType,
        startDateMillis: Long,
        endDateMillis: Long
    ): Flow<Double> {
        return dao.getTotalAmountByTypeAndDate(currentUserId, type.name, startDateMillis, endDateMillis)
            .map { it ?: 0.0 }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        val userId = currentUserId

        val entity = transaction.toEntity(userId = userId, isSynced = false)
        dao.insertTransaction(entity)
        try {
            val dto = entity.toDto()
            remoteDataSource.insertTransaction(dto)
            dao.insertTransaction(entity.copy(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        val userId = currentUserId

        dao.deleteTransaction(transaction.toEntity(userId = userId, isSynced = true))

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
        id = id, // Sua classe Transaction no Domain agora precisa ter o 'id' como String!
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
