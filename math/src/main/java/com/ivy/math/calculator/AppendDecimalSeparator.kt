package com.ivy.math.calculator

fun appendDecimalSeparator(
    expression: String, decimalSeparator: Char
): String {
    fun alreadyAddedDecimal(expression: String): Boolean =
        expression.split(Regex("[^0-9^.]")).last().contains(".")

    fun allowDecimalSeparator(expression: String): Boolean =
        !alreadyAddedDecimal(expression) && when (expression.lastOrNull()) {
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