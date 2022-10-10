package com.ivy.math.calculator

fun appendDecimalSeparator(
    expression: String, decimalSeparator: Char
): String {
    fun allowDecimalSeparator(expression: String): Boolean =
        when (expression.lastOrNull()) {
            ')', decimalSeparator, '%' -> false
            else -> true
        }

    fun appendDecimalSeparator(expression: String): String {
        val lastChar = expression.lastOrNull()
        return when {
            lastChar == null -> expression.plus("0.")
            !lastChar.isDigit() -> expression.plus("0.")
            else -> expression.plus('.')
        }
    }

    return if (allowDecimalSeparator(expression))
        appendDecimalSeparator(expression) else expression
}