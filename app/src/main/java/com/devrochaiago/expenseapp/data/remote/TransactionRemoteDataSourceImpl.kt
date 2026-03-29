package com.devrochaiago.expenseapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions

class TransactionRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TransactionRemoteDataSource {
    private fun getSummaryDocument(userId: String) =
        firestore.collection("users").document(userId).collection("summary").document("metadata")
    private fun getCollection(userId: String) =
        firestore.collection("users").document(userId).collection("transactions")

    override suspend fun getUserSummary(userId: String): UserSummaryDto? {
        val snapshot = getSummaryDocument(userId).get().await()
        return snapshot.toObject(UserSummaryDto::class.java)
    }

    override suspend fun updateUserSummary(userId: String, incomeChange: Double, expenseChange: Double) {
        val docRef = getSummaryDocument(userId)

        val updates = hashMapOf<String, Any>(
            "totalIncome" to FieldValue.increment(incomeChange),
            "totalExpense" to FieldValue.increment(expenseChange)
        )

        docRef.set(updates, SetOptions.merge()).await()
    }
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