package com.ivy.core.domain.algorithm.calc

import com.ivy.common.test.testTimeProvider
import com.ivy.common.time.toUtc
import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType.Expense
import com.ivy.data.transaction.TransactionType.Income
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

class RawStatsTest : FreeSpec({
    "calculate raw stats" {
        val now = testTimeProvider().timeNow().toUtc(testTimeProvider())
        val trns = listOf(
            CalcTrn(amount = 10.0, currency = "EUR", Income, now),
            CalcTrn(amount = 0.01, currency = "EUR", Income, now),
            CalcTrn(amount = 5.0, currency = "BGN", Income, now),
            CalcTrn(amount = 100.0, currency = "BGN", Expense, now.plusSeconds(10)),
            CalcTrn(amount = 1.5, currency = "BGN", Expense, now.plusSeconds(5)),
            CalcTrn(amount = 10_000.0, currency = "USD", Expense, now),
            CalcTrn(amount = 0.005, currency = "BTC", Expense, now),
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
            newestTrnTime = now.plusSeconds(10)
        )
    }

    "sum RawStats" {
        // Arrange
        val a = RawStats(
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
            newestTrnTime = Instant.MIN,
        )
        val b = RawStats(
            incomes = mapOf(
                "USD" to 100.0,
                "BGN" to 1.0,
            ),
            expenses = mapOf(
                "ADA" to 12.0,
                "USD" to 2.0,
            ),
            incomesCount = 6,
            expensesCount = 3,
            newestTrnTime = Instant.MAX,
        )

        // Act
        val res = a + b

        // Assert
        res shouldBe RawStats(
            incomes = mapOf(
                "EUR" to 10.01,
                "BGN" to 6.0,
                "USD" to 100.0,
            ),
            expenses = mutableMapOf(
                "ADA" to 12.0,
                "BTC" to 0.005,
                "USD" to 10_002.0,
                "BGN" to 101.5,
            ),
            incomesCount = 9,
            expensesCount = 7,
            newestTrnTime = Instant.MAX
        )
    }
})