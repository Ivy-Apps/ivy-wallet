package com.ivy.core.domain.calculation.history

import com.ivy.core.data.RecurringRule
import com.ivy.core.data.Transaction
import com.ivy.core.data.calculation.ExchangeRates
import com.ivy.core.data.common.TimeRange
import com.ivy.core.domain.api.data.period.DueDivider
import com.ivy.core.domain.calculation.history.data.RawDueDivider
import com.ivy.core.domain.calculation.history.data.Sorted
import com.ivy.core.domain.calculation.recurring.generateRecurring
import java.util.*

fun groupedDueTransactions(
    rules: List<RecurringRule>,
    dueTransactions: List<Transaction>,
    period: TimeRange,
): SortedMap<RawDueDivider, Sorted<Transaction>> = groupDueTransactions(
    generateDueTransactions(rules, dueTransactions, period)
)

private fun generateDueTransactions(
    rules: List<RecurringRule>,
    dueTransactions: List<Transaction>,
    period: TimeRange,
): List<Transaction> {
    val dueByRule = dueTransactions.groupBy { it.recurring }
    return rules.flatMap {
        generateRecurring(
            rule = it,
            ruleExceptions = dueByRule[it.id] ?: emptyList(),
            period = period,
        )
    }
}

private fun groupDueTransactions(
    dueTransactions: List<Transaction>
): SortedMap<RawDueDivider, Sorted<Transaction>> = TODO()

context(ExchangeRates)
fun exchangeDue(
    rawMap: SortedMap<RawDueDivider, Sorted<Transaction>>
): SortedMap<DueDivider, Sorted<Transaction>> = TODO()