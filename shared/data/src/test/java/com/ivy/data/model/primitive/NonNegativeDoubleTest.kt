package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import org.junit.Test

class NonNegativeDoubleTest {
    @Test
    fun `okay for zero`() {
        NonNegativeDouble.from(0.0).shouldBeRight().value shouldBe 0.0
    }

    @Test
    fun `fails for negative numbers`() {
        NonNegativeDouble.from(-1.0).shouldBeLeft()
    }

    @Test
    fun `positive infinity`() {
        NonNegativeDouble.from(Double.POSITIVE_INFINITY).shouldBeLeft()
    }

    @Test
    fun `negative infinity`() {
        NonNegativeDouble.from(Double.NEGATIVE_INFINITY).shouldBeLeft()
    }

    @Test
    fun `works for positive numbers`() {
        // given
        val number = 42.0

        // when
        val res = NonNegativeDouble.from(number)

        // then
        res.shouldBeRight().value shouldBe 42.0
    }
}
