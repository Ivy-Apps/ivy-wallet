package com.ivy.math

import com.ivy.parser.*
import com.ivy.parser.common.number


/**
 * Evaluates an arbitrary mathematical expression to double.
 */
fun expressionParser(): Parser<Double> = expr()

private fun expr(): Parser<Double> = term().apply { x ->
    char('+').apply {
        expr().apply { y ->
            pure(x + y)
        }
    }
} or term().apply { x ->
    char('-').apply {
        expr().apply { y ->
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
    expr().apply { x ->
        char(')').apply {
            pure(x)
        }
    }
} or string("-(").apply {
    expr().apply { x ->
        char(')').apply {
            pure(-x)
        }
    }
} or string("(-").apply {
    expr().apply { x ->
        char(')').apply {
            pure(-x)
        }
    }
} or number().apply { x ->
    char('%').apply {
        pure(x / 100)
    }
} or number()
