package com.ivy.math.calculator

fun appendDecimalSeparator(
    expression: String, decimalSeparator: Char
): String {
    // this function returns whether the last number in expression already has decimal separator,
    // the function splits the expression on anything which is not a decimal or
    // a decimal separator using the regular expression `[^0-9^${decimalSeparator}]`
    // (^0-9 is for selecting anything which is not a digit and ^${decimalSeparator}
    //  is for selecting anything which is not a decimal separator), to filter out all operators,
    // brackets from the expression and get only numbers in expression,
    // then checks if the last number contains decimal separator.
    // for ex. for expression "1.01+(1.01-2)+1.01" -> split -> [1.01, , 1.01, 2, , 1.01] ->
    // last -> 1.01 -> contains decimal separator? -> true
    fun alreadyAddedDecimal(expression: String): Boolean =
        expression.split(Regex("[^0-9^${decimalSeparator}]")).last()
            .contains(decimalSeparator)

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