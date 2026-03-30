package com.devrochaiago.expenseapp.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val balance: Double = 0.0,
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val showLogoutDialog: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.syncTransactions()
        }
    }

    private val _showLogoutDialog = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllTransactions(),
        _showLogoutDialog
    ) { transactions, showDialog ->

        val income = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val balance = income - expense

        HomeUiState(
            balance = balance,
            income = income,
            expense = expense,
            recentTransactions = transactions.take(10),
            showLogoutDialog = showDialog,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onShowLogoutDialog() {
        _showLogoutDialog.value = true
    }

    fun onDismissLogoutDialog() {
        _showLogoutDialog.value = false
    }
}
