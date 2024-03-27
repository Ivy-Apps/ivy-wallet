package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import org.junit.Test

class PositiveDoubleTest {
    @Test
    fun `fails for zero`() {
        PositiveDouble.from(0.0).shouldBeLeft()
    }

    @Test
    fun `fails for negative numbers`() {
        PositiveDouble.from(-1.0).shouldBeLeft()
    }

    @Test
    fun `positive infinity`() {
        PositiveDouble.from(Double.POSITIVE_INFINITY).shouldBeLeft()
    }

    @Test
    fun `negative infinity`() {
        PositiveDouble.from(Double.NEGATIVE_INFINITY).shouldBeLeft()
    }

    @Test
    fun `works for positive numbers`() {
        // given
        val number = 42.0

        // when
        val res = PositiveDouble.from(number)

        // then
        res.shouldBeRight() shouldBe PositiveDouble.unsafe(42.0)
    }
}
