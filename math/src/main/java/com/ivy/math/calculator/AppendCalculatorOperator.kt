package com.ivy.math.calculator

import com.ivy.math.normalize
import com.ivy.parser.common.decimal

/**
 * Appends calculator option to an expression by following expression syntax.
 * If the calculator option isn't valid it won't be added.
 * @return a new expression with the selected calculator option applied.
 */
fun appendTo(expression: String, option: CalculatorOperator): String = when (option) {
    CalculatorOperator.Plus -> expression.appendPlusOrMinus('+')
    CalculatorOperator.Minus -> expression.appendPlusOrMinus('-')
    CalculatorOperator.Multiply -> expression.appendBinaryOperator('*')
    CalculatorOperator.Divide -> TODO()
    CalculatorOperator.Brackets -> TODO()
    CalculatorOperator.Percent -> TODO()
}

private fun String.appendPlusOrMinus(operator: Char): String = when (this.lastOrNull()) {
    '-', '+' -> this
    else -> this.plus(operator)
}

private fun String.appendBinaryOperator(operator: Char): String {
    /**
     * 10+15.5 => 15.5
     */
    fun lastNumber(expression: String): String? {
        val lastChar = expression.lastOrNull() ?: return expression
        return if (!lastChar.isDigit() && lastChar != '.') {
            // not a part of a decimal, remove it and recurse
            lastNumber(expression.dropLast(1))
        } else expression
    }

    when (this.lastOrNull()) {
        // binary operators can be applied to '%' and ')'
        '%', ')' -> return this.plus(operator)
    }
    val normalizedExpression = normalize(this)
    val lastNumber = lastNumber(normalizedExpression)
        ?: return this // binary expressions require a number on the left!
    val decimalResult = decimal().invoke(lastNumber)
    return if (decimalResult.isNotEmpty()) this.plus(operator) else this
}