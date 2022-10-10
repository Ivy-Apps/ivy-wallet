package com.ivy.math.calculator

import com.ivy.math.expressionParser
import com.ivy.math.normalize
import com.ivy.parser.common.number

/**
 * Appends calculator option to an expression by following expression syntax.
 * If the calculator option isn't valid it won't be added.
 * @return a new expression with the selected calculator option applied.
 */
fun appendTo(expression: String, operator: CalculatorOperator): String = when (operator) {
    CalculatorOperator.Plus -> expression.appendPlusOrMinus('+')
    CalculatorOperator.Minus -> expression.appendPlusOrMinus('-')
    CalculatorOperator.Multiply -> expression.appendBinaryOperator('*')
    CalculatorOperator.Divide -> expression.appendBinaryOperator('/')
    CalculatorOperator.Brackets -> expression.brackets()
    CalculatorOperator.Percent -> expression.percent()
}

private fun String.appendPlusOrMinus(operator: Char): String = when (this.lastOrNull()) {
    '-', '+' -> this.dropLast(1).plus(operator)
    else -> this.plus(operator)
}

private fun String.appendBinaryOperator(operator: Char): String {
    when (this.lastOrNull()) {
        // binary operators can be applied to '%' and ')'
        '%', ')' -> return this.plus(operator)
    }
    // binary operators require a number on the left
    return if (endWithDecimal(this)) this.plus(operator) else this
}

fun bracketsClosed(expression: String): Boolean =
    expression.count { it == '(' } == expression.count { it == ')' }


private fun String.brackets(): String {
    fun determineBracket(expression: String): String {
        if (expression.isEmpty()) return "("
        val closed = bracketsClosed(expression)
        return when (expression.lastOrNull()) {
            '+', '-', '(', '/', '*' -> "("
            ')' -> if (closed) "*(" else ")"
            else -> {
                if (!closed) return ")"
                val parsed = expressionParser().invoke(expression)
                if (parsed.isNotEmpty()) return "*("
                ")"
            }
        }
    }

    return this + determineBracket(this)
}

private fun String.percent(): String {
    fun allowPercent(expression: String): Boolean = when (expression.lastOrNull()) {
        ')' -> true
        null, '+', '-', '*', '/', '%' -> false
        else -> endWithDecimal(this)
    }

    return if (allowPercent(this)) this.plus('%') else this
}

private fun endWithDecimal(expression: String): Boolean {
    /**
     * Extracts the last number from an expression.
     * 10+15.5 => 15.5
     */
    fun lastNumber(expression: String): String? {
        val lastChar = expression.lastOrNull() ?: return null
        return lastChar + (lastNumber(expression.dropLast(1)) ?: "")
    }

    val normalizedExpression = normalize(expression)
    val lastNumber = lastNumber(normalizedExpression)
        ?: return false // binary expressions require a number on the left!
    val decimalResult = number().invoke(lastNumber)
    return decimalResult.isNotEmpty() // parsed successfully a decimal
}