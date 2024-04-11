package com.ivy.data.model.util

import com.ivy.data.model.Value
import com.ivy.data.model.primitive.PositiveDouble
import java.math.BigDecimal
import java.math.RoundingMode

fun Value.round(decimalPlaces: Int): Value = Value(
    amount = PositiveDouble.unsafe(amount.value.roundTo(decimalPlaces)),
    asset = asset,
)

fun Double.roundTo(decimalPlaces: Int): Double {
    return BigDecimal(this).setScale(decimalPlaces, RoundingMode.HALF_EVEN).toDouble()
}