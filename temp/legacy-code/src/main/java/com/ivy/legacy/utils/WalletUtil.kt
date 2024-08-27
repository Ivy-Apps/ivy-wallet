package com.ivy.legacy.utils

fun balancePrefix(
    income: Double,
    expenses: Double
): String? {
    return when {
        expenses != 0.0 && income != 0.0 -> {
            null
        }

        expenses < 0.0 && income == 0.0 -> {
            "-"
        }

        income > 0.0 && expenses == 0.0 -> {
            "+"
        }

        else -> null
    }
}

fun compactBalancePrefix(
    income: Double = 0.0,
    expenses: Double = 0.0
): String {
    val balance = income - expenses
    return when {
        balance > 0 -> "+"
        balance < 0 -> "-"
        else -> ""
    }
}
