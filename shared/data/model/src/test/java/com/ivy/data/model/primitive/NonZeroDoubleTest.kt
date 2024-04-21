package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.filter
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NonZeroDoubleTest {

    @Test
    fun `fails for zero`() {
        NonZeroDouble.from(0.0).shouldBeLeft()
    }

    @Test
    fun `fails for infinity`() {
        NonZeroDouble.from(Double.POSITIVE_INFINITY).shouldBeLeft()
        NonZeroDouble.from(Double.NEGATIVE_INFINITY).shouldBeLeft()
    }

    @Test
    fun `property - valid for all non-zero finite doubles`() = runTest {
        forAll(
            Arb.double(includeNonFiniteEdgeCases = false)
            .filter { it != 0.0 }
        ) { double ->
            NonZeroDouble.from(double).getOrNull()?.value == double
        }
    }
}
