package com.ivy.data.model.testing

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Income
import com.ivy.data.model.TransactionId
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.uuid
import java.time.Instant

fun Arb.Companion.income(
    accountId: Option<AccountId> = None,
    categoryId: Option<CategoryId?> = None,
    settled: Option<Boolean> = None,
    before: Instant = Instant.MAX,
    removed: Option<Boolean> = Some(false),
    amount: Option<PositiveDouble> = None,
    asset: Option<AssetCode> = None,
    id: Option<TransactionId> = None
) = arbitrary {
    Income(
        id = id.getOrElse { Arb.transactionId().bind() },
        title = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        description = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        category = categoryId.getOrElse { Arb.maybe(Arb.categoryId()).bind() },
        time = Arb.instant(maxValue = before).bind(),
        settled = settled.getOrElse { Arb.boolean().bind() },
        metadata = TransactionMetadata(
            recurringRuleId = null,
            loanId = null,
            loanRecordId = null
        ),
        lastUpdated = Instant.EPOCH,
        removed = removed.getOrElse { Arb.boolean().bind() },
        tags = listOf(),
        value = Value(
            amount = amount.getOrElse { Arb.positiveDoubleExact().bind() },
            asset = asset.getOrElse { Arb.assetCode().bind() }
        ),
        account = accountId.getOrElse { Arb.accountId().bind() }

    )
}

fun Arb.Companion.transactionId(): Arb<TransactionId> = Arb.uuid().map(::TransactionId)

fun Arb.Companion.assetCode(): Arb<AssetCode> = Arb.notBlankTrimmedString().map {
    AssetCode.unsafe(it.value)
}