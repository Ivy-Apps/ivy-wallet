package com.ivy.core.domain.calculation.account

import com.ivy.common.test.testTimeProvider
import com.ivy.core.data.AccountId
import com.ivy.core.domain.calculation.expense
import com.ivy.core.domain.calculation.income
import com.ivy.core.domain.calculation.stats
import com.ivy.core.domain.calculation.transfer
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.util.*

class AccountRawStatsTest : FreeSpec({
    val testTimeProvider = testTimeProvider()
    val account = UUID.randomUUID()
    val other = UUID.randomUUID()

    fun transferIn(amount: Double, asset: String) = with(testTimeProvider) {
        transfer(
            fromAccount = other,
            fromAmount = amount,
            fromAsset = asset,
            toAccount = account,
            toAsset = asset,
            toAmount = amount
        )
    }

    fun transferOut(amount: Double, asset: String) = with(testTimeProvider) {
        transfer(
            fromAccount = account,
            fromAmount = amount,
            fromAsset = asset,
            toAccount = other,
            toAsset = asset,
            toAmount = amount
        )
    }


    "calculates account's raw stats" - {
        with(testTimeProvider) {
            withData(
                nameFn = { (caseName, _, _) ->
                    "Case: $caseName"
                },
                row(
                    "Transfers in",
                    listOf(
                        transferIn(100.0, "BGN"),
                        transferIn(50.0, "EUR"),
                    ),
                    stats(
                        incomes = mapOf(
                            "BGN" to 100.0,
                            "EUR" to 50.0,
                        ),
                        expenses = emptyMap(),
                        incomesCount = 2,
                        expensesCount = 0
                    )
                ),
                row(
                    "Transfer out",
                    listOf(
                        transferOut(100.0, "BGN"),
                        transferOut(50.0, "EUR"),
                    ),
                    stats(
                        incomes = emptyMap(),
                        expenses = mapOf(
                            "BGN" to 100.0,
                            "EUR" to 50.0
                        ),
                        incomesCount = 0,
                        expensesCount = 2
                    )
                ),
                row(
                    "Mixed",
                    listOf(
                        income(200.0, "BGN"),
                        transferIn(200.0, "BGN"),
                        expense(50.0, "BGN"),
                        transferOut(100.0, "BGN"),
                        transferOut(20.0, "EUR"),
                    ),
                    stats(
                        incomes = mapOf(
                            "BGN" to 400.0,
                        ),
                        expenses = mapOf(
                            "BGN" to 150.0,
                            "EUR" to 20.0,
                        ),
                        incomesCount = 2,
                        expensesCount = 3
                    )
                ),
            ) { (_, entries, expected) ->
                val res = accountRawStats(AccountId(account), entries)

                res shouldBe expected
            }
        }
    }
})