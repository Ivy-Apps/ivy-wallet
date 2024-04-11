package com.ivy.data.model.testing

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary

const val MAX_ARB_VALUE_ALLOWED = 999_999_999_999.0

fun Arb.Companion.value(
    amount: Option<PositiveDouble> = None,
    asset: Option<AssetCode> = None,
): Arb<Value> = arbitrary {
    Value(
        amount = amount.getOrElse {
            Arb.positiveDoubleExact(max = MAX_ARB_VALUE_ALLOWED).bind()
        },
        asset = asset.getOrElse { Arb.assetCode().bind() }
    )
}