package com.devrochaiago.expenseapp.core.utils

import java.text.NumberFormat
import java.util.Locale

fun Double.toBRL(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(this)
}