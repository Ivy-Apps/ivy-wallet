package com.ivy.data.model.testing

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.property.Arb
import io.kotest.property.arbitrary.ArbitraryBuilderContext
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.removeEdgecases
import io.kotest.property.arbitrary.uuid
import java.time.Instant
import java.util.concurrent.TimeUnit

fun Arb.Companion.income(
    accountId: Option<AccountId> = None,
    categoryId: Option<CategoryId?> = None,
    settled: Option<Boolean> = None,
    time: Option<ArbTime> = None,
    amount: Option<PositiveDouble> = None,
    asset: Option<AssetCode> = None,
    id: Option<TransactionId> = None
): Arb<Income> = arbitrary {
    Income(
        id = id.getOrElse { Arb.transactionId().bind() },
        title = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        description = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        category = categoryId.getOrElse { Arb.maybe(Arb.categoryId()).bind() },
        time = arbInstant(time),
        settled = settled.getOrElse { Arb.boolean().bind() },
        metadata = TransactionMetadata(
            recurringRuleId = null,
            loanId = null,
            paidForDateTime = null,
            loanRecordId = null
        ),
        tags = listOf(),
        value = Arb.value(amount, asset).bind(),
        account = accountId.getOrElse { Arb.accountId().bind() }
    )
}

fun Arb.Companion.expense(
    accountId: Option<AccountId> = None,
    categoryId: Option<CategoryId?> = None,
    settled: Option<Boolean> = None,
    time: Option<ArbTime> = None,
    amount: Option<PositiveDouble> = None,
    asset: Option<AssetCode> = None,
    id: Option<TransactionId> = None
): Arb<Expense> = arbitrary {
    Expense(
        id = id.getOrElse { Arb.transactionId().bind() },
        title = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        description = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        category = categoryId.getOrElse { Arb.maybe(Arb.categoryId()).bind() },
        time = arbInstant(time),
        settled = settled.getOrElse { Arb.boolean().bind() },
        metadata = TransactionMetadata(
            recurringRuleId = null,
            loanId = null,
            paidForDateTime = null,
            loanRecordId = null
        ),
        tags = listOf(),
        value = Arb.value(amount, asset).bind(),
        account = accountId.getOrElse { Arb.accountId().bind() }
    )
}

fun Arb.Companion.transfer(
    categoryId: Option<CategoryId?> = None,
    settled: Option<Boolean> = None,
    time: Option<ArbTime> = None,
    fromAccount: Option<AccountId> = None,
    fromAmount: Option<PositiveDouble> = None,
    fromAsset: Option<AssetCode> = None,
    toAccount: Option<AccountId> = None,
    toAmount: Option<PositiveDouble> = None,
    toAsset: Option<AssetCode> = None,
    id: Option<TransactionId> = None
): Arb<Transfer> = arbitrary {
    val fromAccountVal = fromAccount.getOrElse { Arb.accountId().bind() }
    Transfer(
        id = id.getOrElse { Arb.transactionId().bind() },
        title = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        description = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        category = categoryId.getOrElse { Arb.maybe(Arb.categoryId()).bind() },
        time = arbInstant(time),
        settled = settled.getOrElse { Arb.boolean().bind() },
        metadata = TransactionMetadata(
            recurringRuleId = null,
            loanId = null,
            paidForDateTime = null,
            loanRecordId = null
        ),
        tags = listOf(),
        fromValue = Arb.value(fromAmount, fromAsset).bind(),
        fromAccount = fromAccountVal,
        toAccount = toAccount.getOrElse {
            Arb.accountId().filter { it != fromAccountVal }.bind()
        },
        toValue = Arb.value(toAmount, toAsset).bind()
    )
}

@Suppress("MagicNumber")
fun Arb.Companion.transaction(
    account: Option<AccountId> = None,
    fromAsset: Option<AssetCode> = None,
    toAsset: Option<AssetCode> = None,
): Arb<Transaction> = arbitrary {
    when (Arb.int(1..3).bind()) {
        1 -> income(
            accountId = account,
            asset = fromAsset,
        ).bind()

        2 -> expense(
            accountId = account,
            asset = fromAsset,
        ).bind()

        3 -> transfer(
            fromAccount = account,
            fromAsset = fromAsset,
            toAsset = toAsset,
        ).bind()

        else -> error("Arb.Companion.Transaction error - it's not your test but the test utils.")
    }
}

fun Arb.Companion.transactionId(): Arb<TransactionId> = Arb.uuid().map(::TransactionId)

fun Arb.Companion.assetCode(): Arb<AssetCode> = Arb.notBlankTrimmedString().map {
    AssetCode.unsafe(it.value)
}

@Suppress("MagicNumber")
private suspend fun ArbitraryBuilderContext.arbInstant(
    time: Option<ArbTime>
): Instant {
    // Because of legacy timezone conversions
    val safeOffset = TimeUnit.SECONDS.toDays(365 * 4)
    val safeMaxValue = Instant.MAX.minusSeconds(safeOffset)
    val safeMinValue = Instant.MIN.plusSeconds(safeOffset)
    return when (time) {
        None -> Arb.instant(
            minValue = safeMinValue,
            maxValue = safeMaxValue
        ).removeEdgecases().bind()

        is Some -> when (val arbTime = time.value) {
            is ArbTime.Before -> Arb.instant(
                minValue = safeMinValue,
                maxValue = minOf(arbTime.before, safeMaxValue)
            ).bind()

            is ArbTime.After -> Arb.instant(
                minValue = maxOf(arbTime.after, safeMinValue),
                maxValue = safeMaxValue
            ).bind()

            is ArbTime.Exactly -> arbTime.time
        }
    }
}

sealed interface ArbTime {
    data class Before(val before: Instant) : ArbTime
    data class After(val after: Instant) : ArbTime
    data class Exactly(val time: Instant) : ArbTime
}