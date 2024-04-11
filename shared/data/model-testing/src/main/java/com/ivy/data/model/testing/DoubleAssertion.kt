package com.ivy.data.model.testing

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

infix fun Double.shouldBeApprox(other: Double) {
    val tolerance = other * 0.000001
    this shouldBe (other plusOrMinus tolerance)
}
