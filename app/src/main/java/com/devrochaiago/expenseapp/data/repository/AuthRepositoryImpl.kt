package com.devrochaiago.expenseapp.data.repository

import com.devrochaiago.expenseapp.di.AppModule.ApplicationScope
import com.devrochaiago.expenseapp.domain.model.User
import com.devrochaiago.expenseapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationScope private val externalScope: CoroutineScope
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
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
    }

    override suspend fun register(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
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
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
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
