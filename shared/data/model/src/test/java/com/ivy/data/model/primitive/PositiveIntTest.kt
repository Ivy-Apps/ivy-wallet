package com.ivy.data.model.primitive

import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PositiveIntTest {
    @Test
    fun `valid when GT 0`() = runTest {
        forAll(Arb.int(min = 1)) { number ->
            PositiveInt.from(number).getOrNull()?.value == number
        }
    }

    @Test
    fun `invalid when LTE 0`() = runTest {
        forAll(Arb.int(max = 0)) { number ->
            PositiveInt.from(number).isLeft()
        }
    }
}