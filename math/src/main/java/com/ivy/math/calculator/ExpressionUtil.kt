package com.ivy.math.calculator

import com.ivy.math.normalize

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