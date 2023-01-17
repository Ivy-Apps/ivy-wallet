package com.ivy.core.domain.algorithm.calc

import com.ivy.core.domain.algorithm.calc.data.CalcTrn
import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionType

fun rawStats(trns: List<CalcTrn>): RawStats {
    val incomes = mutableMapOf<CurrencyCode, Double>()
    val expenses = mutableMapOf<CurrencyCode, Double>()
    var incomesCount = 0
    var expensesCount = 0

    trns.forEach { trn ->
        when (trn.type) {
            TransactionType.Income -> {
                incomesCount++
                incomes.sumCurrency(trn)
            }
            TransactionType.Expense -> {
                expensesCount++
                expenses.sumCurrency(trn)
            }
        }
    }

    return RawStats(
        incomes = incomes,
        expenses = expenses,
        incomesCount = incomesCount,
        expensesCount = expensesCount,
    )
}

private fun MutableMap<CurrencyCode, Double>.sumCurrency(
    trn: CalcTrn
) {
    compute(trn.currency) { _, oldValue ->
        (oldValue ?: 0.0) + trn.amount
    }
}