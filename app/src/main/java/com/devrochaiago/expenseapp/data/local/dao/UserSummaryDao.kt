package com.devrochaiago.expenseapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devrochaiago.expenseapp.data.local.entity.UserSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSummary(summary: UserSummaryEntity)

    @Query("SELECT * FROM user_summary WHERE userId = :userId")
    fun getUserSummaryFlow(userId: String): Flow<UserSummaryEntity?>

    @Query("SELECT * FROM user_summary WHERE userId = :userId")
    suspend fun getUserSummary(userId: String): UserSummaryEntity?
}