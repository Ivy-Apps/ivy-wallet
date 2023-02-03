package com.ivy.core.domain.calculation.account

import com.ivy.common.test.testTimeProvider
import com.ivy.core.data.AccountId
import com.ivy.core.domain.calculation.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
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

    "transfers in-out property" {
        with(testTimeProvider) {
            val arbPair = arbitrary {
                val amount = Arb.int(min = 1).bind().toDouble()
                val asset = Arb.assetCode().bind().code
                val expense = Arb.boolean().bind()
                if (expense) {
                    transferOut(amount, asset) to expense(amount, asset)
                } else {
                    transferIn(amount, asset) to income(amount, asset)
                }
            }

            // PROPERTY: Transfer-In = Income && Transfer-Out = Expense
            checkAll(Arb.list(arbPair)) { input ->
                val accountId = AccountId(account)
                val transfers = input.map { it.first }
                val singles = input.map { it.second }

                rawStatsEquality(
                    accountRawStats(accountId, transfers),
                    accountRawStats(accountId, singles)
                )
            }
        }
    }
})