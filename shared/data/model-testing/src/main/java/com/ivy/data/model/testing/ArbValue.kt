package com.ivy.data.model.testing

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import com.ivy.data.model.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.model.util.VALUE_DECIMAL_PLACES_PRECISION
import com.ivy.data.model.util.roundTo
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary

const val MAX_ARB_VALUE_ALLOWED = 999_999_999_999.0

fun Arb.Companion.value(
    amount: Option<PositiveDouble> = None,
    asset: Option<AssetCode> = None,
): Arb<Value> = arbitrary {
    Value(
        amount = amount.getOrElse {
            val positiveDouble = Arb.positiveDoubleExact(max = MAX_ARB_VALUE_ALLOWED).bind()
            PositiveDouble.unsafe(
                positiveDouble.value.roundTo(VALUE_DECIMAL_PLACES_PRECISION) +
                        // ensure that it won't become 0 after rounding
                        "0.${"0".repeat(VALUE_DECIMAL_PLACES_PRECISION - 1)}1".toDouble()
            )
        },
        asset = asset.getOrElse { Arb.assetCode().bind() }
    )
}