package com.devrochaiago.expenseapp.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devrochaiago.expenseapp.ui.components.BalanceCard
import com.devrochaiago.expenseapp.ui.components.TransactionCard
import com.devrochaiago.expenseapp.ui.theme.ExpenseAppTheme

// Classe temporária para Mock de Dados
data class MockTransaction(
    val id: Int,
    val title: String,
    val category: String,
    val date: String,
    val amount: String,
    val isIncome: Boolean,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // Mock Data para a nossa lista
    val recentTransactions = listOf(
        MockTransaction(1, "Supermercado Extra", "Alimentação", "Hoje", "R$ 450,00", false, Icons.Default.ShoppingCart),
        MockTransaction(2, "Salário Tech Lead", "Renda", "Ontem", "R$ 12.000,00", true, Icons.Default.Work),
        MockTransaction(3, "Uber", "Transporte", "17 de Março", "R$ 35,50", false, Icons.Default.DirectionsCar),
        MockTransaction(4, "Ifood Lanche", "Alimentação", "16 de Março", "R$ 68,90", false, Icons.Default.Fastfood),
        MockTransaction(5, "Freela de App", "Renda", "15 de Março", "R$ 2.500,00", true, Icons.Default.Work)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navegar para tela de adicionar transação */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar Transação")
            }
        }
    ) { paddingValues ->

        // LazyColumn é a melhor prática para listas de dados.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp) // Espaço extra para não sobrepor o FAB no final da lista
        ) {

            // Header e Balance Card
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Saudação simples
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Nosso Componente de Saldo
                    BalanceCard(
                        balance = "R$ 13.995,60",
                        income = "R$ 14.500,00",
                        expense = "R$ 554,40"
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Título da Seção de Transações
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
                        TextButton(onClick = { /* TODO: Ir para aba de todas transações */ }) {
                            Text(text = "Ver tudo")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Lista de Transações Recentes usando o TransactionCard da Fase 1
            items(
                items = recentTransactions,
                key = { it.id } // Usar key otimiza as animações e performance da LazyColumn
            ) { transaction ->
                TransactionCard(
                    title = transaction.title,
                    category = transaction.category,
                    date = transaction.date,
                    amount = transaction.amount,
                    isIncome = transaction.isIncome,
                    icon = transaction.icon,
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
        HomeScreen()
    }
}