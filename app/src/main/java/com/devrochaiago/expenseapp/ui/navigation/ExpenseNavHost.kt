package com.devrochaiago.expenseapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devrochaiago.expenseapp.ui.feature.addtransaction.AddTransactionRoute
import com.devrochaiago.expenseapp.ui.feature.home.HomeRoute
import com.devrochaiago.expenseapp.ui.feature.statistics.StatisticsRoute
import com.devrochaiago.expenseapp.ui.feature.transactions.TransactionsRoute

@Composable
fun ExpenseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeRoute(
                onNavigateToAddTransaction = {
                    navController.navigate("add_transaction")
                },
                onNavigateToTransactions = {
                    navController.navigate("transactions")
                },
                onNavigateToStatistics = {
                    navController.navigate("statistics")
                }
            )
        }
        composable("transactions") {
            TransactionsRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("add_transaction") {
            AddTransactionRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("statistics") {
            StatisticsRoute()
        }
    }
}
