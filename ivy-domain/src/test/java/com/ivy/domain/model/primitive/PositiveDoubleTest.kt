package com.ivy.domain.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
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
    }

    "works for positive numbers" {
        PositiveDouble.from(42.0)
            .isRight { it.value == 42.0 } shouldBe true
    }
})