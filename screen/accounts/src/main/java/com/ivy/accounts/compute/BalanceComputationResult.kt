package com.ivy.accounts.compute

data class BalanceComputationResult(
    val incomeInScopedTimeRange: Double,
    val expenseInScopedTimeRange: Double,
    val transferIncomeInScopedTimeRange: Double,
    val transferExpenseInScopedTimeRange: Double,
    val balance: Double,
    val balanceInBaseCurrency: Double
)
