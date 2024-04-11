package com.ivy.data.model.testing

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DoubleApproxAssertionTest {

    enum class DoubleApproxTestCase(
        val a: Double,
        val b: Double,
        val approxTheSame: Boolean
    ) {
        ExactlyTheSame(
            a = 3.14,
            b = 3.14,
            approxTheSame = true
        ),
        ApproxPI(
            a = 3.14,
            b = 3.13999999,
            approxTheSame = true
        ),
        NotApproxPI(
            a = 3.14,
            b = 3.135,
            approxTheSame = false
        ),
        RealMoneyNotApprox(
            a = 100.0,
            b = 99.99,
            approxTheSame = false
        ),
        RealMoneyApprox(
            a = 100.0,
            b = 99.9999999,
            approxTheSame = true
        ),
        RealMoneyBigNotApprox(
            a = 1_000_000.0,
            b = 999_999.0,
            approxTheSame = false
        ),
        RealMoneyBigApprox(
            a = 1_000_000.0,
            b = 999_999.9,
            approxTheSame = true
        ),
    }

    @Test
    fun `double approx assertion`(
        @TestParameter testCase: DoubleApproxTestCase
    ) {
        // given
        val a = testCase.a
        val b = testCase.b

        // when
        val approxTheSame = try {
            a shouldBeApprox b
            true
        } catch (e: AssertionError) {
            false
        }

        // then
        approxTheSame shouldBe testCase.approxTheSame
    }

    @Test
    fun `property - approx same values are always the same when scaled`() = runTest {
        // given
        val a = 1.0
        val b = 0.99999999
        a shouldBeApprox b

        checkAll(Arb.nonZeroScalar()) { scalar ->
            // when
            val scaledA = a * scalar
            val scaledB = b * scalar

            // then
            scaledA shouldBeApprox scaledB
        }
    }

    @Test
    fun `property - not approx same values are always diff when scaled`() = runTest {
        // given
        val a = 1.0
        val b = 0.99
        a shouldNotBeApprox b

        checkAll(Arb.nonZeroScalar()) { scalar ->
            // when
            val scaledA = a * scalar
            val scaledB = b * scalar

            // then
            scaledA shouldNotBeApprox scaledB
        }
    }

    private fun Arb.Companion.nonZeroScalar(): Arb<Int> = Arb.int(
        min = -MaxArbValueAllowed.toInt(),
        max = MaxArbValueAllowed.toInt()
    ).filter { it != 0 }
}