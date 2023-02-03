package com.ivy.core.domain.calculation

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.data.AccountId
import com.ivy.core.data.AccountValue
import com.ivy.core.data.common.AssetCode
import com.ivy.core.data.common.NonNegativeInt
import com.ivy.core.data.common.PositiveDouble
import com.ivy.core.data.common.Value
import com.ivy.core.data.optimized.LedgerEntry
import com.ivy.core.domain.data.RawStats
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.util.*

context(TimeProvider)
fun income(
    amount: Double,
    asset: String,
    time: LocalDateTime = timeNow(),
) = LedgerEntry.Single.Income(
    Value(PositiveDouble.of(amount), AssetCode.of(asset)),
    time = time
)

context(TimeProvider)
fun expense(
    amount: Double,
    asset: String,
    time: LocalDateTime = timeNow(),
) = LedgerEntry.Single.Expense(
    Value(PositiveDouble.of(amount), AssetCode.of(asset)),
    time = time
)

context(TimeProvider)
fun transfer(
    fromAccount: UUID,
    fromAmount: Double,
    fromAsset: String,
    toAccount: UUID,
    toAmount: Double,
    toAsset: String,
    time: LocalDateTime = timeNow(),
) = LedgerEntry.Transfer(
    from = AccountValue(
        AccountId(fromAccount), Value(PositiveDouble.of(fromAmount), AssetCode.of(fromAsset))
    ),
    to = AccountValue(
        AccountId(toAccount), Value(PositiveDouble.of(toAmount), AssetCode.of(toAsset))
    ),
    time = time,
)

context(TimeProvider)
fun stats(
    incomes: Map<String, Double>,
    expenses: Map<String, Double>,
    incomesCount: Int,
    expensesCount: Int,
    newestTransaction: LocalDateTime = timeNow(),
): RawStats {
    fun fixMap(map: Map<String, Double>) =
        map.map { (key, value) -> AssetCode.of(key) to PositiveDouble.of(value) }.toMap()

    return RawStats(
        incomes = fixMap(incomes),
        expenses = fixMap(expenses),
        incomesCount = NonNegativeInt.of(incomesCount),
        expensesCount = NonNegativeInt.of(expensesCount),
        newestTransaction = newestTransaction,
    )
}

fun rawStatsEquality(
    res1: RawStats,
    res2: RawStats,
) {
    res1.incomes shouldContainExactly res2.incomes
    res1.expenses shouldContainExactly res2.expenses
    res1.incomesCount shouldBe res2.incomesCount
    res1.expensesCount shouldBe res2.expensesCount
    res1.newestTransaction shouldBe res2.newestTransaction
}