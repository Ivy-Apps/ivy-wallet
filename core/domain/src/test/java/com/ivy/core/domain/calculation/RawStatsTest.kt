package com.ivy.core.domain.calculation

import com.ivy.common.test.testTimeProvider
import com.ivy.core.data.AccountId
import com.ivy.core.data.AccountValue
import com.ivy.core.data.common.PositiveDouble
import com.ivy.core.data.common.Value
import com.ivy.core.data.optimized.LedgerEntry
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.time.LocalDateTime
import java.util.*

class RawStatsTest : FreeSpec({
    val testTimeProvider = testTimeProvider()

    "calculates raw stats w/o transfers" - {
        with(testTimeProvider) {
            withData(
                nameFn = { (entries, _) ->
                    "Case with ${entries.size} entries"
                },
                row(
                    listOf(
                        income(10.0, "BGN"),
                        income(90.0, "BGN"),
                        income(50.0, "EUR"),
                        expense(25.0, "USD"),
                        expense(75.0, "USD"),
                        expense(100.0, "BGN"),
                        expense(1.5, "EUR"),
                    ),
                    stats(
                        incomes = mapOf(
                            "BGN" to 100.0,
                            "EUR" to 50.0
                        ),
                        expenses = mapOf(
                            "USD" to 100.0,
                            "BGN" to 100.0,
                            "EUR" to 1.5,
                        ),
                        incomesCount = 3,
                        expensesCount = 4,
                    )
                ),
                row(
                    listOf(
                        income(10_000.0, "BGN"),
                        expense(4_500.0, "BGN")
                    ),
                    stats(
                        incomes = mapOf("BGN" to 10_000.0),
                        expenses = mapOf("BGN" to 4_500.0),
                        incomesCount = 1,
                        expensesCount = 1,
                    )
                ),
                row(
                    emptyList(),
                    stats(
                        incomes = emptyMap(),
                        expenses = emptyMap(),
                        incomesCount = 0,
                        expensesCount = 0,
                        newestTransaction = LocalDateTime.MIN,
                    )
                )
            ) { (entries, expected) ->
                for (i in 1..10) {
                    // Calculation property: same input => same output
                    // also test with different entries order

                    val res = rawStats(
                        // entries order must not effect the calculation
                        entries = entries.shuffled(),
                        interpretTransfer = { emptyList() }
                    )

                    res shouldBe expected
                }
            }
        }
    }

    "newest transaction time property" {
        with(testTimeProvider) {
            val newestTime = Arb.localDateTime().next()

            // region Setup
            // Generates entry before newest
            val arbEntry = arbitrary {
                val arbTime = Arb.localDateTime(
                    maxLocalDateTime = newestTime.minusSeconds(1)
                ).bind()
                if (Arb.boolean().bind()) {
                    expense(1.0, "TEST", time = arbTime)
                } else {
                    income(1.0, "TEST", time = arbTime)
                }
            }

            val arbEntries = arbitrary {
                val randomEntries = Arb.list(arbEntry, range = 0..100).bind()
                val newestEntry = if (Arb.boolean().bind()) {
                    expense(1.0, "TEST", newestTime)
                } else {
                    income(1.0, "TEST", newestTime)
                }
                (randomEntries + newestEntry).shuffled()
            }
            // endregion

            // PROPERTY: The newest transaction is the one with largest epoch seconds
            checkAll(arbEntries) { entries ->
                val calculated = rawStats(entries, interpretTransfer = { emptyList() })
                calculated.newestTransaction shouldBe newestTime
            }
        }
    }

    "interpret transfer property" {
        with(testTimeProvider) {
            // Setup
            val arbAmount = arbitrary {
                val value = Arb.positiveInt().bind().toDouble()
                PositiveDouble.of(value)
            }

            val arbInput = arbitrary {
                val assetCode = Arb.assetCode().bind()
                val expense = arbAmount.bind()
                val income = arbAmount.bind()
                val time = Arb.localDateTime().bind()

                LedgerEntry.Transfer(
                    from = AccountValue(
                        AccountId(UUID.randomUUID()), Value(expense, assetCode)
                    ),
                    to = AccountValue(
                        AccountId(UUID.randomUUID()), Value(income, assetCode)
                    ),
                    time = time
                ) to listOf(
                    expense(expense.value, assetCode.code, time = time),
                    income(income.value, assetCode.code, time = time),
                )
            }

            // PROPERTY: Transfers can be mapped to Income-Expense with a interpretation
            checkAll(Arb.list(arbInput)) { input ->
                val transfers = input.map { it.first }.shuffled()
                val entries = input.flatMap { it.second }.shuffled()

                val res1 = rawStats(transfers, interpretTransfer = {
                    listOf(
                        expense(
                            it.from.value.amount.value,
                            it.from.value.asset.code,
                            time = it.time
                        ),
                        income(
                            it.to.value.amount.value,
                            it.to.value.asset.code,
                            time = it.time
                        )
                    )
                })
                val res2 = rawStats(entries, interpretTransfer = { emptyList() })

                rawStatsEquality(res1, res2)
            }
        }
    }
})