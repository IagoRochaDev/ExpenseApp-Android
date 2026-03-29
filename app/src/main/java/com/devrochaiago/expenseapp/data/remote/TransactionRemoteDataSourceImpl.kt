package com.devrochaiago.expenseapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TransactionRemoteDataSource {

    private fun getCollection(userId: String) =
        firestore.collection("users").document(userId).collection("transactions")

    override suspend fun insertTransaction(transaction: TransactionDto) {
        getCollection(transaction.userId)
            .document(transaction.id)
            .set(transaction)
            .await()
    }

    override suspend fun updateTransaction(transaction: TransactionDto) {
        getCollection(transaction.userId)
            .document(transaction.id)
            .set(transaction)
            .await()
    }

    override suspend fun deleteTransaction(userId: String, transactionId: String) {
        getCollection(userId)
            .document(transactionId)
            .delete()
            .await()
    }

    override suspend fun getAllTransactions(userId: String, limit: Long?): List<TransactionDto> {
        var query = getCollection(userId).orderBy("dateMillis", Query.Direction.DESCENDING)

        if (limit != null) {
            query = query.limit(limit)
        }

        val snapshot = query.get().await()
        return snapshot.documents.mapNotNull { it.toObject(TransactionDto::class.java) }
    }
}