package com.devrochaiago.expenseapp.ui.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devrochaiago.expenseapp.core.utils.getCategoryIcon
import com.devrochaiago.expenseapp.core.utils.toBRL
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.ui.components.BalanceCard
import com.devrochaiago.expenseapp.ui.components.TransactionCard
import com.devrochaiago.expenseapp.ui.theme.ExpenseAppTheme
import com.devrochaiago.expenseapp.core.utils.toRelativeDateString
@Composable
fun HomeRoute(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onAddClick = onNavigateToAddTransaction ,
        onStatisticsClick = onNavigateToStatistics,
        onTransactionsClick = onNavigateToTransactions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAddClick: () -> Unit,
    onStatisticsClick: () -> Unit = {},
    onTransactionsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddClick() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar Transação")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Olá, Iago",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bem-vindo de volta!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedIconButton(
                            onClick = onStatisticsClick,
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Estatísticas",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    BalanceCard(
                        balance = uiState.balance.toBRL(),
                        income = uiState.income.toBRL(),
                        expense = uiState.expense.toBRL()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Transações Recentes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = onTransactionsClick) {
                            Text(text = "Ver Todas")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            items(
                uiState.recentTransactions,
                key = { it.id }
            ) { transaction ->
                TransactionCard(
                    title = transaction.title,
                    category = transaction.category,
                    date = transaction.dateMillis.toRelativeDateString(),
                    amount = transaction.amount.toBRL(),
                    isIncome = transaction.type == TransactionType.INCOME,
                    icon = getCategoryIcon(transaction.category),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

//@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun HomeScreenPreview() {
    ExpenseAppTheme {
        HomeScreen(
            uiState = HomeUiState(),
            onAddClick = {},
            onTransactionsClick = {},
            onStatisticsClick = {}
        )
    }
}
