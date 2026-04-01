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
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
)

sealed class AuthEvent {
    data class LoginWithEmail(val email: String, val pass: String) : AuthEvent()
    data class RegisterWithEmail(val email: String, val pass: String) : AuthEvent()
    data class LoginWithGoogle(val idToken: String) : AuthEvent()
    object ResetError : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.LoginWithEmail -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    val result = authRepository.login(event.email, event.pass)
                    result.onSuccess { user ->
                        _uiState.update { it.copy(isLoading = false, user = user) }
                    }.onFailure { exception ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
                    }
                }
            }

            is AuthEvent.RegisterWithEmail -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    val result = authRepository.register(event.email, event.pass)
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
        }
    }
}
