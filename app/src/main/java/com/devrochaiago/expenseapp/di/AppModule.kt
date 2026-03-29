package com.devrochaiago.expenseapp.di

import android.app.Application
import androidx.room.Room
import com.devrochaiago.expenseapp.data.local.ExpenseDatabase
import com.devrochaiago.expenseapp.data.local.dao.TransactionDao
import com.devrochaiago.expenseapp.data.remote.TransactionRemoteDataSource
import com.devrochaiago.expenseapp.data.remote.TransactionRemoteDataSourceImpl
import com.devrochaiago.expenseapp.data.repository.TransactionRepositoryImpl
import com.devrochaiago.expenseapp.domain.repository.AuthRepository
import com.devrochaiago.expenseapp.domain.repository.TransactionRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideExpenseDatabase(app: Application): ExpenseDatabase {
        return Room.databaseBuilder(
            app,
            ExpenseDatabase::class.java,
            "expense_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: ExpenseDatabase): TransactionDao {
        return db.transactionDao()
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        dao: TransactionDao,
        remoteDataSource: TransactionRemoteDataSource,
        authRepository: AuthRepository
    ): TransactionRepository {
        return TransactionRepositoryImpl(dao, remoteDataSource, authRepository)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideTransactionRemoteDataSource(
        firestore: FirebaseFirestore
    ): TransactionRemoteDataSource {
        return TransactionRemoteDataSourceImpl(firestore)
    }
}
