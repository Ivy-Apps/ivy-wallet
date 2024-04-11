package com.ivy.data.model.testing

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.abs

/**
 * Defines a constant representing the percentage tolerance for approximation checks.
 */
const val PercentageTolerance = 0.0000001 // Represents 0.00001%

/**
 * Asserts that the numeric value of the receiver [Double] is approximately equal
 * to another [Double], considering a tolerance threshold.
 *
 * The tolerance is dynamically calculated as [PercentageTolerance] of
 * the larger (by magnitude) of the two values being compared.
 * This method is useful for scenarios where floating-point precision
 * might lead to minor discrepancies between
 * theoretically equal values, such as in financial calculations or when dealing
 * with the results of complex numerical operations.
 *
 * @param other The [Double] to compare the receiver to.
 * @throws AssertionError if the difference between the receiver
 * and [other] exceeds the calculated tolerance.
 */
infix fun Double.shouldBeApprox(other: Double) {
    // Calculate tolerance as a percentage of the larger (by absolute value) of the two numbers.
    val tolerance = maxOf(abs(this), abs(other)) * PercentageTolerance
    // Calculate the absolute difference between the two values.
    val difference = abs(this - other)

    // Assert that the difference is within the calculated tolerance.
    difference shouldBe (0.0 plusOrMinus tolerance)
}

/**
 * Same as [Double.shouldBeApprox] but negated.
 */
infix fun Double.shouldNotBeApprox(other: Double) {
    // Calculate tolerance as a percentage of the larger (by absolute value) of the two numbers.
    val tolerance = maxOf(abs(this), abs(other)) * PercentageTolerance
    // Calculate the absolute difference between the two values.
    val difference = abs(this - other)

    // Assert that the difference is not within the calculated tolerance.
    difference shouldNotBe (0.0 plusOrMinus tolerance)
}
