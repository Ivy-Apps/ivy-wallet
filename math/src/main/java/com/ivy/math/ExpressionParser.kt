package com.ivy.math

import com.ivy.parser.*
import com.ivy.parser.common.applySign
import com.ivy.parser.common.decimal
import com.ivy.parser.common.optionalNumberSign

/**
 * Evaluates an arbitrary mathematical expression to double.
 */
fun expressionParser(): Parser<Double> = term().apply { x ->
    char('+').apply {
        expressionParser().apply { y ->
            pure(x + y)
        }
    }
} or term().apply { x ->
    char('-').apply {
        expressionParser().apply { y ->
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

private fun factor(): Parser<Double> = optionalNumberSign().apply { sign ->
    char('(').apply {
        expressionParser().apply { x ->
            char(')').apply {
                pure(x.applySign(sign))
            }
        }
    }
} or optionalNumberSign().apply { sing ->
    char('(').apply {
        decimal().apply { x ->
            char(')').apply {
                pure(x.applySign(sing))
            }
        }
    }
} or decimal()
