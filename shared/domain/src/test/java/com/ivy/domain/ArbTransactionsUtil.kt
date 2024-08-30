package com.ivy.domain

import arrow.core.NonEmptyList
import arrow.core.Some
import com.ivy.data.model.AccountId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonNegativeInt
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.model.testing.assetCode
import com.ivy.data.model.testing.expense
import com.ivy.data.model.testing.income
import com.ivy.data.model.testing.transfer
import com.ivy.data.model.testing.value
import com.ivy.domain.model.StatSummary
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.nonNegativeInt

fun Arb.Companion.nonEmptyTransfersOut(
    account: AccountId,
    asset: AssetCode,
    min: Int = 1,
    max: Int = 100,
): Arb<NonEmptyList<Transfer>> = transfersOut(
    account = account,
    asset = asset,
    min = min.coerceAtLeast(1),
    max = max
).map { it.toNonEmptyList() }

fun Arb.Companion.nonEmptyTransfersIn(
    account: AccountId,
    asset: AssetCode,
    min: Int = 1,
    max: Int = 100,
): Arb<NonEmptyList<Transfer>> = transfersIn(
    account = account,
    asset = asset,
    min = min.coerceAtLeast(1),
    max = max
).map { it.toNonEmptyList() }

fun Arb.Companion.nonEmptyIncomes(
    account: AccountId,
    asset: AssetCode,
    min: Int = 1,
    max: Int = 100,
): Arb<NonEmptyList<Income>> = incomes(
    account = account,
    asset = asset,
    min = min.coerceAtLeast(1),
    max = max
).map { it.toNonEmptyList() }

fun Arb.Companion.nonEmptyExpenses(
    account: AccountId,
    asset: AssetCode,
    min: Int = 1,
    max: Int = 100,
): Arb<NonEmptyList<Expense>> = expenses(
    account = account,
    asset = asset,
    min = min.coerceAtLeast(1),
    max = max
).map { it.toNonEmptyList() }

fun Arb.Companion.incomes(
    account: AccountId,
    asset: AssetCode,
    min: Int = 0,
    max: Int = 100,
): Arb<List<Income>> = Arb.list(
    gen = Arb.income(
        accountId = Some(account),
        asset = Some(asset)
    ),
    range = min..max
)

fun Arb.Companion.expenses(
    account: AccountId,
    asset: AssetCode,
    min: Int = 0,
    max: Int = 100,
): Arb<List<Expense>> = Arb.list(
    gen = Arb.expense(
        accountId = Some(account),
        asset = Some(asset)
    ),
    range = min..max
)

fun Arb.Companion.transfersOut(
    account: AccountId,
    asset: AssetCode,
    min: Int = 0,
    max: Int = 100,
): Arb<List<Transfer>> = Arb.list(
    gen = Arb.transfer(
        fromAccount = Some(account),
        fromAsset = Some(asset)
    ),
    range = min..max
)

fun Arb.Companion.transfersIn(
    account: AccountId,
    asset: AssetCode,
    min: Int = 0,
    max: Int = 100,
): Arb<List<Transfer>> = Arb.list(
    gen = Arb.transfer(
        toAccount = Some(account),
        toAsset = Some(asset)
    ),
    range = min..max
)

fun Arb.Companion.statSummary(
): Arb<StatSummary> {
    val transactions = mutableMapOf<AssetCode, PositiveDouble>()
     assetCode().samples().map {
         transactions[it.value] = PositiveDouble.unsafe((1..Int.MAX_VALUE).random().toDouble())
    }
    return Arb.bind(
        Arb.nonNegativeInt(),
        Arb.map(keyArb = assetCode(), valueArb = Arb.value().map { it.amount })
    ) { _, trns ->
        val nonNegativeInt = if(trns.isNotEmpty()) {
            NonNegativeInt.unsafe(trns.size)
        } else {
            NonNegativeInt.Zero
        }
        StatSummary(nonNegativeInt, trns)
    }
}