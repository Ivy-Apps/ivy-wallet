package com.ivy.core.domain.algorithm.calc

import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.domain.algorithm.calc.data.Stats
import com.ivy.data.Value
import com.ivy.data.exchange.ExchangeRates
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExchangeRawStatsTest : FreeSpec({
    "exchange raw stats" - {
        // Arrange
        val rawStats = RawStats(
            incomes = mutableMapOf(
                "BGN" to 5.0
            ),
            incomesCount = 1,
            expenses = mutableMapOf(
                "EUR" to 5.0,
                "BGN" to 10.0,
            ),
            expensesCount = 2
        )
        val rates = ExchangeRates(
            baseCurrency = "BGN",
            rates = mutableMapOf(
                // 1 BGN = 0.5 EUR
                "EUR" to 0.5
            )
        )

        // Act
        val res = exchangeRawStats(
            rawStats = rawStats,
            rates = rates,
            outputCurrency = "BGN"
        )

        // Assert
        res shouldBe Stats(
            income = Value(5.0, "BGN"),
            expense = Value(20.0, "BGN"),
            incomesCount = 1,
            expensesCount = 2,
        )
    }
})