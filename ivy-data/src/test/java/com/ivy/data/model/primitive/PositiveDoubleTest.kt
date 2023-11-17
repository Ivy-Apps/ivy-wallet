package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class PositiveDoubleTest : FreeSpec({
    "fails for" - {
        "zero [0]" {
            PositiveDouble.from(0.0).shouldBeLeft()
        }
        "negative numbers" {
            PositiveDouble.from(-1.0).shouldBeLeft()
        }
        "positive infinity" {
            PositiveDouble.from(Double.POSITIVE_INFINITY).shouldBeLeft()
        }
        "negative infinity" {
            PositiveDouble.from(Double.NEGATIVE_INFINITY).shouldBeLeft()
        }
    }

    "works for positive numbers" {
        // given
        val number = 42.0

        // when
        val res = PositiveDouble.from(number)

        // then
        res.shouldBeRight() shouldBe PositiveDouble(42.0)
    }
})