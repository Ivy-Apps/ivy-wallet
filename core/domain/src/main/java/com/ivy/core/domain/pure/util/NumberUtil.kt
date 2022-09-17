package com.ivy.core.domain.pure.util

import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt

fun isSignificant(number: Double) = abs(number) > 0.009

// region Split double into int part and decimal part
data class SplitDouble(
    val intPart: Int,
    val decimalPart: Double
)

fun split(number: Double): SplitDouble {
    val numberStr = number.toString()
    val numberBigDecimal = BigDecimal(numberStr)
    val intPart: Int = numberBigDecimal.toInt()
    val decimalPart = numberBigDecimal.subtract(BigDecimal(intPart)).toDouble()
    return SplitDouble(
        intPart = intPart,
        decimalPart = decimalPart
    )
}
// endregion

// region Shorten big numbers, 10,500.50 => 10,5k
fun shorten(number: Double): String {
    fun formatShortened(shortened: Double, magnitude: String): String {
        val decimalPart = split(shortened).decimalPart
        return if (isSignificant(decimalPart)) {
            val df = DecimalFormat("###,##0.00")
            "${df.format(shortened)}$magnitude"
        } else {
            "${shortened.roundToInt()}$magnitude"
        }
    }

    return when {
        abs(number) >= 1_000_000 -> {
            formatShortened(number / 1_000_000, "m")
        }
        abs(number) >= 1_000 -> {
            formatShortened(number / 1_000, "k")
        }
        else -> number.toString()
    }
}
// endregion