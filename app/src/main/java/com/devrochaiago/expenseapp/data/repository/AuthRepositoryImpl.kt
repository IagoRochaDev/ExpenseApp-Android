package com.devrochaiago.expenseapp.data.repository

import com.devrochaiago.expenseapp.domain.model.User
import com.devrochaiago.expenseapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // O .await() é a mágica do Kotlin que espera o Firebase sem travar a tela!
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null && user.email != null) {
                Result.success(User(id = user.uid, email = user.email!!))
            } else {
                Result.failure(Exception("Usuário não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null && user.email != null) {
                Result.success(User(id = user.uid, email = user.email!!))
            } else {
                Result.failure(Exception("Erro ao criar usuário"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            // 1. Cria a credencial do Firebase usando o Token que o Google nos deu
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // 2. Faz o login no Firebase com essa credencial
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null && user.email != null) {
                Result.success(User(id = user.uid, email = user.email!!))
            } else {
                Result.failure(Exception("Erro ao finalizar login com Google"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        val user = firebaseAuth.currentUser
        return if (user != null && user.email != null) {
            User(id = user.uid, email = user.email!!)
        } else null
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}