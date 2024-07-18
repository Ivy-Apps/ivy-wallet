package com.ivy.data.model.testing

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.primitive.AssetCode
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.uuid

fun Arb.Companion.account(
    accountId: Option<AccountId> = None,
    asset: Option<AssetCode> = None,
    includeInBalance: Option<Boolean> = None,
    orderNum: Option<Double> = None,
): Arb<Account> = arbitrary {
    Account(
        id = accountId.getOrElse { Arb.accountId().bind() },
        name = Arb.notBlankTrimmedString().bind(),
        asset = asset.getOrElse { Arb.assetCode().bind() },
        color = Arb.colorInt().bind(),
        icon = Arb.maybe(Arb.iconAsset()).bind(),
        includeInBalance = includeInBalance.getOrElse { Arb.boolean().bind() },
        orderNum = orderNum.getOrElse { Arb.double().bind() },
    )
}

fun Arb.Companion.accountId(): Arb<AccountId> = Arb.uuid().map(::AccountId)
