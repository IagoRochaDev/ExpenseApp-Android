package com.devrochaiago.expenseapp.ui.feature.statistics

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devrochaiago.expenseapp.core.utils.getCategoryColor
import com.devrochaiago.expenseapp.core.utils.getCategoryIcon
import com.devrochaiago.expenseapp.core.utils.toBRL
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StatisticsUiState(
    val categoryStats: List<CategoryStat> = emptyList(),
    val totalExpense: Float = 0f,
    val isLoading: Boolean = true
)

data class CategoryStat(
    val id: Int,
    val name: String,
    val amount: Float,
    val percentage: Float,
    val formattedAmount: String,
    val color: Color,
    val icon: ImageVector
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = repository.getAllTransactions()
        .map { transactions ->

            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

            val totalExpense = expenses.sumOf { it.amount }.toFloat()

            val groupedExpenses = expenses.groupBy { it.category }

            val statsList = groupedExpenses.entries.mapIndexed { index, entry ->
                val categoryName = entry.key
                val categoryTotal = entry.value.sumOf { it.amount }.toFloat()
                val percentage = if (totalExpense > 0) (categoryTotal / totalExpense) else 0f

                CategoryStat(
                    id = index,
                    name = categoryName,
                    amount = categoryTotal,
                    percentage = percentage,
                    formattedAmount = categoryTotal.toDouble().toBRL(),
                    color = getCategoryColor(categoryName),
                    icon = getCategoryIcon(categoryName)
                )
            }.sortedByDescending { it.amount }

            StatisticsUiState(
                categoryStats = statsList,
                totalExpense = totalExpense,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatisticsUiState(isLoading = true)
        )
}
