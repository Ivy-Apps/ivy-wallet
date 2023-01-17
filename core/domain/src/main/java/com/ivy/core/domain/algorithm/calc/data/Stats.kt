package com.ivy.core.domain.algorithm.calc.data

import com.ivy.data.Value

/**
 * [RawStats] exchanged in an output currency.
 *
 * @param incomesCount the count of the income transactions
 * @param expense the count of the expense transactions
 */
data class Stats(
    val income: Value,
    val expense: Value,
    val incomesCount: Int,
    val expensesCount: Int,
)