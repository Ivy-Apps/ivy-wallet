package com.ivy.math.calculator

/**
 * Appends calculator option to an expression by following expression syntax.
 * If the calculator option isn't valid it won't be added.
 * @return a new expression with the selected calculator option applied.
 */
fun appendTo(expression: String, option: CalculatorOption): String = when (option) {
    CalculatorOption.Plus -> expression.appendPlus()
    CalculatorOption.Minus -> TODO()
    CalculatorOption.Multiply -> TODO()
    CalculatorOption.Divide -> TODO()
    CalculatorOption.Brackets -> TODO()
    CalculatorOption.Percent -> TODO()
    CalculatorOption.Equals -> TODO()
    CalculatorOption.C -> TODO()
}

private fun String.appendPlus(): String = when (this.lastOrNull()) {
    '-', '+' -> this
    else -> this.plus('+')
}