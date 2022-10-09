package com.ivy.math

fun evaluate(expression: String): Double? {
    val parser = expression()
    val result = parser(normalize(expression))
    return result.firstOrNull()?.takeIf { it.leftover.isEmpty() }?.value
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
