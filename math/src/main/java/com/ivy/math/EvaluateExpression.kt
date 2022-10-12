package com.ivy.math

import com.ivy.math.calculator.bracketsClosed
import timber.log.Timber

fun evaluate(expression: String): Double? {
    val parser = expressionParser()
    val fixedExpression = tryFixExpression(normalize(expression))
    val result = parser(fixedExpression)
    val expressionTree = result.firstOrNull()
        ?.takeIf { it.leftover.isEmpty() }?.value ?: return null
    Timber.d("Evaluating: ${expressionTree.print()}")
    return expressionTree.eval()
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

    fun fixLeadingPlus(expression: String): String = if (expression.firstOrNull() == '+')
        expression.drop(1) else expression

    var fixBrackets = fixLeadingPlus(expression)
        .let(::fixPartialBinaryOps)
        .replace("(+", "(")
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
