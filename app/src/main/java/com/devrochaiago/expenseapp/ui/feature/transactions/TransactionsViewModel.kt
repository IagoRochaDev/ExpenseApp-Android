package com.devrochaiago.expenseapp.ui.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<TransactionsUiState> = repository.getAllTransactions()
        .map { transactions ->
            TransactionsUiState(
                transactions = transactions.sortedByDescending { it.dateMillis },
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionsUiState(isLoading = true)
        )
}
