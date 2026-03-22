package com.devrochaiago.expenseapp.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Long.toRelativeDateString(): String {
    val calendarNow = Calendar.getInstance() // Pega o dia de hoje
    val calendarTransaction = Calendar.getInstance().apply { timeInMillis = this@toRelativeDateString } // Pega o dia da transação

    val yearNow = calendarNow.get(Calendar.YEAR)
    val dayOfYearNow = calendarNow.get(Calendar.DAY_OF_YEAR)

    val yearTransaction = calendarTransaction.get(Calendar.YEAR)
    val dayOfYearTransaction = calendarTransaction.get(Calendar.DAY_OF_YEAR)

    return when {
        yearNow == yearTransaction && dayOfYearNow == dayOfYearTransaction -> "Hoje"

        yearNow == yearTransaction && (dayOfYearNow - dayOfYearTransaction) == 1 -> "Ontem"

        yearNow == yearTransaction -> {
            val format = SimpleDateFormat("dd MMM", Locale("pt", "BR"))
            format.format(Date(this)).replaceFirstChar { it.uppercase() }
        }

        else -> {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            format.format(Date(this))
        }
    }
}