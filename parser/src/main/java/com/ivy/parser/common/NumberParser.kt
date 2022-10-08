package com.ivy.parser.common

import com.ivy.parser.*

fun digit(): Parser<Char> = sat { it.isDigit() }

private enum class NumberType {
    Positive, Negative
}

private fun plusMinus(): Parser<NumberType> = zeroOrOne((char('+') or char('-'))).apply { symbol ->
    pure(
        when (symbol) {
            null, '+' -> NumberType.Positive
            '-' -> NumberType.Negative
            else -> error("impossible")
        }
    )
}

/**
 * Parses an integer number (..., -1, 0, 1, ...).
 */
fun int(): Parser<Int> = plusMinus().apply { numberType ->
    oneOrMany(digit()).apply { digits ->
        val number = digits.joinToString(separator = "").toInt()
        pure(
            when (numberType) {
                NumberType.Positive -> number
                NumberType.Negative -> -number
            }
        )
    }
}