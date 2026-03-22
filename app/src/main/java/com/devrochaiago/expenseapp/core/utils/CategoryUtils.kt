package com.devrochaiago.expenseapp.core.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Alimentação" -> Icons.Default.Fastfood
        "Transporte" -> Icons.Default.DirectionsCar
        "Lazer" -> Icons.Default.Movie
        "Contas" -> Icons.Default.Receipt
        else -> Icons.Default.AccountBalanceWallet
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Alimentação" -> Color(0xFFF59E0B)
        "Transporte" -> Color(0xFF3B82F6)
        "Lazer" -> Color(0xFF8B5CF6)
        "Contas" -> Color(0xFFEF4444)
        else -> Color(0xFF10B981)
    }
}
