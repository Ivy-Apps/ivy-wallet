package com.ivy.math.calculator

fun appendTo(expression: String, digit: Int): String {
    val thingToAppend = when (expression.lastOrNull()) {
        ')', '%' -> "*$digit"
        else -> "$digit"
    }
    return expression.plus(thingToAppend)
}