package com.ivy.wallet.utils

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