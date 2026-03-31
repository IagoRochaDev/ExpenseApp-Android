package com.devrochaiago.expenseapp.di

import android.app.Application
import androidx.room.Room
import com.devrochaiago.expenseapp.data.local.ExpenseDatabase
import com.devrochaiago.expenseapp.data.local.dao.TransactionDao
import com.devrochaiago.expenseapp.data.local.dao.UserSummaryDao
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier

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
    fun provideUserSummaryDao(db: ExpenseDatabase): UserSummaryDao {
        return db.userSummaryDao()
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        summaryDao: UserSummaryDao,
        remoteDataSource: TransactionRemoteDataSource,
        authRepository: AuthRepository,
        @ApplicationScope externalScope: CoroutineScope
    ): TransactionRepository {
        return TransactionRepositoryImpl(
            transactionDao,
            summaryDao,
            remoteDataSource,
            authRepository,
            externalScope
        )
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

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class ApplicationScope

    @Module
    @InstallIn(SingletonComponent::class)
    object CoroutinesModule {
        @Provides
        @Singleton
        @ApplicationScope
        fun providesApplicationScope(): CoroutineScope {
            return CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }
    }
}
