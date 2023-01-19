package com.ivy.core.domain.action.calculate

import com.ivy.data.Value

/**
 * Data type representing [CalculateFlow]'s result.
 * @param balance it's equal to [income] - [expense]
 * @param income the sum of income transactions
 * @param expense the sum of expense transactions
 * @param incomesCount the # of income transactions or simply how many incomes there were as a count
 * @param expensesCount the # of expense transactions or
 * simply how many expenses there were as a count
 */
@Deprecated("inefficient - will be replaced with `account-cache` algo")
data class Stats(
    val balance: Value,
    val income: Value,
    val expense: Value,
    val incomesCount: Int,
    val expensesCount: Int,
)