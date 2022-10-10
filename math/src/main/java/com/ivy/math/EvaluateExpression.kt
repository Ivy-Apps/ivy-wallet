package com.ivy.math

import com.ivy.math.calculator.bracketsClosed

fun evaluate(expression: String): Double? {
    val parser = expressionParser()
    val fixedExpression = tryFixExpression(normalize(expression))
    val result = parser(fixedExpression)
    return result.firstOrNull()?.takeIf { it.leftover.isEmpty() }?.value
}

fun tryFixExpression(expression: String): String {
    fun fixPartialBinaryOps(expression: String): String = when (expression.lastOrNull()) {
        '+', '-', '*', '/' -> expression.dropLast(1)
        else -> when {
            expression.endsWith("()") -> fixPartialBinaryOps(expression.dropLast(2))
            expression.endsWith("(") -> fixPartialBinaryOps(expression.dropLast(1))
            else -> expression
        }
    }

    val fixBinaryOperators = fixPartialBinaryOps(expression)
    var fixBrackets = fixBinaryOperators
    while (!bracketsClosed(fixBrackets)) {
        fixBrackets += ')'
    }
    return fixBrackets.replace("()", "") // fix empty brackets
}

/**
 * Returns a normalized expression by:
 * - removing grouping separators for thousands
 * - replacing local decimal separator with '.'
 * 1,032.55 => 1032.55
 */
fun normalize(expression: String): String = expression
    .replace(localGroupingSeparator().toString(), "")
    .replace(localDecimalSeparator().toString(), ".")
