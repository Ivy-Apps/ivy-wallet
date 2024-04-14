package com.ivy.data.model.primitive

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NonNegativeIntTest {
    @Test
    fun `valid when GTE 0`() = runTest {
        forAll(Arb.int(min = 0)) { number ->
            NonNegativeInt.from(number).getOrNull()?.value == number
        }
    }

    @Test
    fun `invalid when LT 0`() = runTest {
        forAll(Arb.int(max = -1)) { number ->
            NonNegativeInt.from(number).isLeft()
        }
    }

    @Test
    fun `zero is valid`() = runTest {
        NonNegativeInt.Zero.value shouldBe 0
    }
}