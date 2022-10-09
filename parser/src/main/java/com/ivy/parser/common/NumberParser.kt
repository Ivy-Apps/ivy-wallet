package com.ivy.parser.common

import com.ivy.parser.*

fun digit(): Parser<Char> = sat { it.isDigit() }

enum class NumberSign {
    Positive, Negative
}

fun optionalNumberSign(): Parser<NumberSign> =
    optional((char('+') or char('-'))).apply { symbol ->
        pure(
            when (symbol) {
                null, '+' -> NumberSign.Positive
                '-' -> NumberSign.Negative
                else -> error("impossible")
            }
        )
    }

/**
 * Parses an integer number without a sign.
 */
fun unsignedInt(): Parser<Int> = oneOrMany(digit()).apply { digits ->
    val number = digits.joinToString(separator = "").toInt()
    pure(number)
}

/**
 * Parses an integer number (..., -1, 0, 1, ...).
 */
fun int(): Parser<Int> = optionalNumberSign().apply { sign ->
    unsignedInt().apply { number ->
        pure(number.applySign(sign))
    }
}

/**
 * Parses a decimal number from as a string as double.
 *
 * **Supported formats:**
 * - 3.14, 1024.0 _"#.#"_
 * - .5, .9 _".#"_
 * - "3." 15. _"#."_
 * - 3, 5, 8 _"#"_
 */
fun decimal(): Parser<Double> {
    fun oneOrMoreDigits(): Parser<String> = oneOrMany(digit()).apply { digits ->
        pure(digits.joinToString(separator = ""))
    }

    return optionalNumberSign().apply { sign ->
        // 3.14, ###.00
        unsignedInt().apply { intPart ->
            char('.').apply {
                oneOrMoreDigits().apply { decimalPart ->
                    pure("$intPart.$decimalPart".toDouble().applySign(sign))
                }
            }
        }
    } or optionalNumberSign().apply { sign ->
        // .5 => 0.5
        char('.').apply {
            oneOrMoreDigits().apply { decimalPart ->
                pure("0.$decimalPart".toDouble().applySign(sign))
            }
        }
    } or optionalNumberSign().apply { sign ->
        // 3. => 3.0
        unsignedInt().apply { intPart ->
            char('.').apply {
                pure(intPart.toDouble().applySign(sign))
            }
        }
    } or int().apply { pure(it.toDouble()) } // 3, 5, 13
}

// region Util
private fun Int.applySign(sign: NumberSign): Int = when (sign) {
    NumberSign.Positive -> this
    NumberSign.Negative -> -this
}

fun Double.applySign(sign: NumberSign): Double = when (sign) {
    NumberSign.Positive -> this
    NumberSign.Negative -> -this
}
// endregion