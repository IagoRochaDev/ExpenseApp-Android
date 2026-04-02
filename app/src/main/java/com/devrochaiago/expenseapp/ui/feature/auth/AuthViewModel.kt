package com.devrochaiago.expenseapp.ui.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devrochaiago.expenseapp.domain.model.User
import com.devrochaiago.expenseapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isCheckingUser: Boolean = true,
    val user: User? = null,
    val errorMessage: String? = null
)

sealed class AuthEvent {
    data class OnEmailChange(val email: String) : AuthEvent()
    data class OnPasswordChange(val password: String) : AuthEvent()
    object LoginClick : AuthEvent()
    object RegisterClick : AuthEvent()
    data class LoginWithGoogle(val idToken: String) : AuthEvent()
    object ResetError : AuthEvent()
    object CheckCurrentUser : AuthEvent()
    data class ToggleLoading(val isLoading: Boolean) : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        onEvent(AuthEvent.CheckCurrentUser)
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.OnEmailChange -> {
                _uiState.update { it.copy(email = event.email, errorMessage = null) }
            }

            is AuthEvent.OnPasswordChange -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }

            AuthEvent.LoginClick -> {
                login()
            }

            AuthEvent.RegisterClick -> {
                register()
            }

            is AuthEvent.LoginWithGoogle -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    val result = authRepository.loginWithGoogle(event.idToken)
                    result.onSuccess { user ->
                        _uiState.update { it.copy(isLoading = false, user = user) }
                    }.onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Erro no Google"
                            )
                        }
                    }
                }
            }

            AuthEvent.ResetError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            AuthEvent.CheckCurrentUser -> {
                checkCurrentUser()
            }

            is AuthEvent.ToggleLoading -> {
                _uiState.update { it.copy(isLoading = event.isLoading) }
            }
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingUser = true) }
            val currentUser = authRepository.getCurrentUser()
            _uiState.update { it.copy(isCheckingUser = false, user = currentUser) }
        }
    }

    private fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, user = user) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
            }
        }
    }

    private fun register() {
        val email = _uiState.value.email
        val password = _uiState.value.password
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(email, password)
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, user = user) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro desconhecido"
                    )
                }
            }
        }
    }
}
