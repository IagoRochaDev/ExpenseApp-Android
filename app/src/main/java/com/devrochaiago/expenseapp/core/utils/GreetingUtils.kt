package com.devrochaiago.expenseapp.core.utils

import java.util.Calendar

fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 6..11 -> "Bom dia!"
        in 12..17 -> "Boa tarde!"
        else -> "Boa noite!"
    }
}