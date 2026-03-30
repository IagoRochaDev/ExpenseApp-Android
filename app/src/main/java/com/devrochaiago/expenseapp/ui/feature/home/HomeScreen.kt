package com.devrochaiago.expenseapp.ui.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.devrochaiago.expenseapp.core.utils.getGreeting
import com.devrochaiago.expenseapp.core.utils.toBRL
import com.devrochaiago.expenseapp.core.utils.toRelativeDateString
import com.devrochaiago.expenseapp.domain.model.TransactionType
import com.devrochaiago.expenseapp.ui.components.BalanceCard
import com.devrochaiago.expenseapp.ui.components.TransactionCard
import com.devrochaiago.expenseapp.ui.theme.ExpenseAppTheme

@Composable
fun HomeRoute(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onAddClick = onNavigateToAddTransaction,
        onStatisticsClick = onNavigateToStatistics,
        onTransactionsClick = onNavigateToTransactions,
        onLogoutClick = onNavigateToLogin,
        onShowLogoutDialog = viewModel::onShowLogoutDialog,
        onHideLogoutDialog = viewModel::onDismissLogoutDialog
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAddClick: () -> Unit,
    onStatisticsClick: () -> Unit = {},
    onTransactionsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onShowLogoutDialog: () -> Unit,
    onHideLogoutDialog: () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Transação"
                )
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = getGreeting(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Visão geral das suas finanças",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                OutlinedIconButton(
                                    onClick = onStatisticsClick,
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = "Estatísticas"
                                    )
                                }

                                OutlinedIconButton(
                                    onClick = onShowLogoutDialog,
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sair",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Transações Recentes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onTransactionsClick) {
                                Text("Ver Todas")
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

            if (uiState.showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = onHideLogoutDialog,
                    title = {
                        Text("Sair do aplicativo")
                    },
                    text = {
                        Text("Tem certeza que deseja desconectar sua conta?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onHideLogoutDialog()
                                onLogoutClick()
                            }
                        ) {
                            Text("Sair", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onHideLogoutDialog) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true
)
@Composable
private fun HomeScreenPreview() {
    ExpenseAppTheme {
        HomeScreen(
            uiState = HomeUiState(),
            onAddClick = {},
            onTransactionsClick = {},
            onStatisticsClick = {},
            onShowLogoutDialog = {},
            onHideLogoutDialog = {}
        )
    }
}
