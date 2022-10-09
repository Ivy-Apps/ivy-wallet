package com.ivy.math

import com.ivy.parser.*
import com.ivy.parser.common.decimal

/**
 * Evaluates an arbitrary mathematical expression to double.
 */
fun expression(): Parser<Double> = term().apply { x ->
    char('+').apply {
        expression().apply { y ->
            pure(x + y)
        }
    }
} or term().apply { x ->
    char('-').apply {
        expression().apply { y ->
            pure(x - y)
        }
    }
} or term()

private fun term(): Parser<Double> = factor().apply { x ->
    char('%').apply {
        pure(x / 100)
    }
} or factor().apply { x ->
    char('*').apply {
        term().apply { y ->
            pure(x * y)
        }
    }
} or factor().apply { x ->
    char('/').apply {
        term().apply { y ->
            pure(x / y)
        }
    }
} or factor()

private fun factor(): Parser<Double> = char('(').apply {
    expression().apply { x ->
        char(')').apply {
            pure(x)
        }
    }
} or decimal()
