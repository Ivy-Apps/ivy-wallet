package com.ivy.parser.common

import com.ivy.parser.*

fun digit(): Parser<Char> = sat { it.isDigit() }

private enum class NumberSign {
    Positive, Negative
}

private fun numberSign(): Parser<NumberSign> =
    optional((char('+') or char('-'))).apply { symbol ->
        pure(
            when (symbol) {
                null, '+' -> NumberSign.Positive
                '-' -> NumberSign.Negative
                else -> error("impossible")
            }
        )
    }

fun unsignedInt(): Parser<Int> = oneOrMany(digit()).apply { digits ->
    val number = digits.joinToString(separator = "").toInt()
    pure(number)
}

/**
 * Parses an integer number (..., -1, 0, 1, ...).
 */
fun int(): Parser<Int> = numberSign().apply { sign ->
    unsignedInt().apply { number ->
        pure(number.applySign(sign))
    }
}

fun decimal(): Parser<Double> {
    fun decimalPart(): Parser<String> = oneOrMany(digit()).apply { digits ->
        pure(digits.joinToString(separator = ""))
    }

    return numberSign().apply { sign ->
        unsignedInt().apply { intPart ->
            char('.').apply {
                decimalPart().apply { decimalPart ->
                    pure("$intPart.$decimalPart".toDouble().applySign(sign))
                }
            }
        }
    } or numberSign().apply { sign ->
        char('.').apply {
            decimalPart().apply { decimalPart ->
                pure("0.$decimalPart".toDouble().applySign(sign))
            }
        }
    } or numberSign().apply { sign ->
        unsignedInt().apply { intPart ->
            char('.').apply {
                pure(intPart.toDouble().applySign(sign))
            }
        }
    } or int().apply { pure(it.toDouble()) }
}

// region Util
private fun Int.applySign(sign: NumberSign): Int = when (sign) {
    NumberSign.Positive -> this
    NumberSign.Negative -> -this
}

private fun Double.applySign(sign: NumberSign): Double = when (sign) {
    NumberSign.Positive -> this
    NumberSign.Negative -> -this
}
// endregion