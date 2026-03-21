package com.devrochaiago.expenseapp.data.repository

import com.devrochaiago.expenseapp.data.local.dao.TransactionDao
import com.devrochaiago.expenseapp.data.local.entity.TransactionEntity
import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTotalAmountByTypeAndDate(
        type: TransactionType,
        startDateMillis: Long,
        endDateMillis: Long
    ): Flow<Double> {
        return dao.getTotalAmountByTypeAndDate(type.name, startDateMillis, endDateMillis)
            .map { it ?: 0.0 }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction.toEntity())
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

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        title = title,
        amount = amount,
        category = category,
        type = type.name,
        dateMillis = dateMillis
    )
}
