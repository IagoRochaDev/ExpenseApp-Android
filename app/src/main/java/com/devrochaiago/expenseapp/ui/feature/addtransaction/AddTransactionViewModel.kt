package com.devrochaiago.expenseapp.ui.feature.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devrochaiago.expenseapp.domain.model.Transaction
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTransactionUiState(
    val title: String = "",
    val amount: String = "",
    val category: String = "Alimentação",
    val isExpense: Boolean = true,
    val dateMillis: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)

sealed class AddTransactionEvent {
    data class OnTitleChange(val title: String) : AddTransactionEvent()
    data class OnAmountChange(val amount: String) : AddTransactionEvent()
    data class OnTypeChange(val isExpense: Boolean) : AddTransactionEvent()
    data class OnCategoryChange(val category: String) : AddTransactionEvent()
    data class OnDateChange(val dateMillis: Long) : AddTransactionEvent()
    data class OnSaveClick(val onSuccess: () -> Unit) : AddTransactionEvent()
}

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.OnTitleChange -> {
                _uiState.update { it.copy(title = event.title) }
            }

            is AddTransactionEvent.OnAmountChange -> {
                val filtered = event.amount.filter { it.isDigit() || it == '.' || it == ',' }
                _uiState.update { it.copy(amount = filtered) }
            }

            is AddTransactionEvent.OnTypeChange -> {
                val defaultCategory = if (event.isExpense) "Alimentação" else "Salário"
                _uiState.update {
                    it.copy(
                        isExpense = event.isExpense,
                        category = defaultCategory
                    )
                }
            }

            is AddTransactionEvent.OnCategoryChange -> {
                _uiState.update { it.copy(category = event.category) }
            }

            is AddTransactionEvent.OnDateChange -> {
                _uiState.update { it.copy(dateMillis = event.dateMillis) }
            }

            is AddTransactionEvent.OnSaveClick -> {
                saveTransaction(event.onSuccess)
            }
        }
    }

    private fun saveTransaction(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.isLoading) return

        val amountDouble = currentState.amount.replace(",", ".").toDoubleOrNull() ?: 0.0

        if (currentState.title.isBlank() || amountDouble <= 0.0) {
            return
        }
        _uiState.update { it.copy(isLoading = true) }

        val transaction = Transaction(
            title = currentState.title,
            amount = amountDouble,
            category = currentState.category,
            type = if (currentState.isExpense) TransactionType.EXPENSE else TransactionType.INCOME,
            dateMillis = currentState.dateMillis
        )

        viewModelScope.launch {
            repository.insertTransaction(transaction)
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
