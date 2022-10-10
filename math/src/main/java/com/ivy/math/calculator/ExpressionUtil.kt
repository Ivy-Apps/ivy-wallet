package com.ivy.math.calculator

import com.ivy.math.localDecimalSeparator
import com.ivy.math.normalize
import java.text.DecimalFormat

/**
 * @return whether the calculation result is worth to be displayed.
 */
fun hasObviousResult(expression: String, value: Double?): Boolean =
    when (expression.lastOrNull()) {
        '+', '-', '*', '/' -> expression.dropLast(1).none {
            // It's obvious if it has any preceding calculations
            when (it) {
                '+', '-', '*', '/' -> true
                else -> false
            }
        }
        else -> normalize(expression).toDoubleOrNull() == value
    }

fun beautify(expression: String): String? {
    fun formatInt(number: String): String =
        number.toIntOrNull()?.let { DecimalFormat("###,###,###").format(it) } ?: number

    fun format(number: String): String = if (number.contains(localDecimalSeparator())) {
        // format decimal
        val (int, decimal) = number.split(localDecimalSeparator())
        "${formatInt(int)}.$decimal"
    } else {
        formatInt(number)
    }

    if (expression.isEmpty()) return null
    var beautified = expression
    expression.split("+", "-", "*", "/", "(", ")", "%")
        .forEach { numberStr ->
            val formattedNum = format(numberStr)
            beautified = beautified.replace(numberStr, formattedNum)
        }
    return beautified
}