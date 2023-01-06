package com.ivy.parser.common

import com.ivy.parser.*

fun digit(): Parser<Char> = sat { it.isDigit() }

/**
 * Parses an integer number without a sign.
 */
fun int(): Parser<Int> = oneOrMany(digit()).apply { digits ->
    val number = try {
        digits.joinToString(separator = "").toInt()
    } catch (e: Exception) {
        Int.MAX_VALUE
    }
    pure(number)
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
fun number(): Parser<Double> {
    fun oneOrMoreDigits(): Parser<String> = oneOrMany(digit()).apply { digits ->
        pure(digits.joinToString(separator = ""))
    }

    return int().apply { intPart ->
        // 3.14, ###.00
        char('.').apply {
            oneOrMoreDigits().apply { decimalPart ->
                pure("$intPart.$decimalPart".toDouble())
            }
        }
    } or char('.').apply {
        // .5 => 0.5
        oneOrMoreDigits().apply { decimalPart ->
            pure("0.$decimalPart".toDouble())
        }
    } or int().apply { intPart ->
        // 3. => 3.0
        char('.').apply {
            pure(intPart.toDouble())
        }
    } or int().apply {
        // 3, 5, 13
        pure(it.toDouble())
    }
}