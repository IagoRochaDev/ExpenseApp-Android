package com.devrochaiago.expenseapp.ui.feature.statistics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private fun getCategoryColor(category: String): Color {
        return when (category) {
            "Alimentação" -> Color(0xFFF59E0B)
            "Transporte" -> Color(0xFF3B82F6)
            "Lazer" -> Color(0xFF8B5CF6)
            "Contas" -> Color(0xFFEF4444)
            else -> Color(0xFF10B981)
        }
    }

    private fun getCategoryIcon(category: String): ImageVector {
        return when (category) {
            "Alimentação" -> Icons.Default.Fastfood
            "Transporte" -> Icons.Default.DirectionsCar
            "Lazer" -> Icons.Default.Movie
            "Contas" -> Icons.Default.Receipt
            else -> Icons.Default.AccountBalanceWallet
        }
    }
}
