package com.ivy.core.domain.algorithm.calc

import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class RawStatsTest : FreeSpec({
    "calculate raw stats" - {
        val trns = listOf(
            CalcTrn(amount = 10.0, currency = "EUR", TransactionType.Income),
            CalcTrn(amount = 0.01, currency = "EUR", TransactionType.Income),
            CalcTrn(amount = 5.0, currency = "BGN", TransactionType.Income),
            CalcTrn(amount = 100.0, currency = "BGN", TransactionType.Expense),
            CalcTrn(amount = 1.5, currency = "BGN", TransactionType.Expense),
            CalcTrn(amount = 10_000.0, currency = "USD", TransactionType.Expense),
            CalcTrn(amount = 0.005, currency = "BTC", TransactionType.Expense),
        ).shuffled()

        val res = rawStats(trns)

        res shouldBe RawStats(
            incomes = mapOf(
                "EUR" to 10.01,
                "BGN" to 5.0,
            ),
            expenses = mapOf(
                "BGN" to 101.5,
                "USD" to 10_000.0,
                "BTC" to 0.005
            ),
            incomesCount = 3,
            expensesCount = 4,
        )
    }
})