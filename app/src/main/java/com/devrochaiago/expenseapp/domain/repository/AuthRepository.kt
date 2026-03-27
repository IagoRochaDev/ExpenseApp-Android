package com.devrochaiago.expenseapp.domain.repository

import com.devrochaiago.expenseapp.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String): Result<User>

    suspend fun loginWithGoogle(idToken: String): Result<User>

    fun getCurrentUser(): User?
    fun logout()
}