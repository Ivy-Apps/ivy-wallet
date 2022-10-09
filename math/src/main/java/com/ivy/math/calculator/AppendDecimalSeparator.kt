package com.ivy.math.calculator

fun appendDecimalSeparator(
    expression: String, decimalSeparator: Char
): String {
    fun allowDecimalSeparator(expression: String): Boolean =
        when (expression.lastOrNull()) {
            ')', decimalSeparator, '%' -> false
            else -> true
        }

    return if (allowDecimalSeparator(expression))
        expression.plus(decimalSeparator) else expression
}