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

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onAmountChange(newAmount: String) {
        val filtered = newAmount.filter { it.isDigit() || it == '.' || it == ',' }
        _uiState.update { it.copy(amount = filtered) }
    }

    fun onTypeChange(isExpense: Boolean) {
        val defaultCategory = if (isExpense) "Alimentação" else "Salário"
        _uiState.update { it.copy(isExpense = isExpense, category = defaultCategory) }
    }

    fun onCategoryChange(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun onDateChange(newDateMillis: Long) {
        _uiState.update { it.copy(dateMillis = newDateMillis) }
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.isLoading) return

        val amountDouble = currentState.amount.replace(",", ".").toDoubleOrNull() ?: 0.0

        if (currentState.title.isBlank() || amountDouble <= 0.0) {
            return
        }
        _uiState.value = currentState.copy(isLoading = true)

        val transaction = Transaction(
            title = currentState.title,
            amount = amountDouble,
            category = currentState.category,
            type = if (currentState.isExpense) TransactionType.EXPENSE else TransactionType.INCOME,
            dateMillis = currentState.dateMillis
        )

        viewModelScope.launch {
            repository.insertTransaction(transaction)
            _uiState.value = _uiState.value.copy(isLoading = false)
            onSuccess()
        }
    }
}