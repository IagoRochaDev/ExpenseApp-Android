package com.devrochaiago.expenseapp.di

import android.app.Application
import androidx.room.Room
import com.devrochaiago.expenseapp.data.local.ExpenseDatabase
import com.devrochaiago.expenseapp.data.local.dao.TransactionDao
import com.devrochaiago.expenseapp.data.repository.TransactionRepositoryImpl
import com.devrochaiago.expenseapp.domain.repository.TransactionRepository
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
    fun provideTransactionRepository(dao: TransactionDao): TransactionRepository {
        return TransactionRepositoryImpl(dao)
    }
}
