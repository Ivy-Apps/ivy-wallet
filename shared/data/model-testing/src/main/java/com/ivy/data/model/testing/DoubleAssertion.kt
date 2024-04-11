package com.ivy.data.model.testing

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.abs

const val PERCENTAGE_TOLERANCE = 0.0000001 // 0.00001%

infix fun Double.shouldBeApprox(other: Double) {
    val tolerance = maxOf(abs(this), abs(other)) * PERCENTAGE_TOLERANCE
    val difference = abs(this - other)

    difference shouldBe (0.0 plusOrMinus tolerance)
}

infix fun Double.shouldNotBeApprox(other: Double) {
    val tolerance = maxOf(abs(this), abs(other)) * PERCENTAGE_TOLERANCE
    val difference = abs(this - other)

    difference shouldNotBe (0.0 plusOrMinus tolerance)
}