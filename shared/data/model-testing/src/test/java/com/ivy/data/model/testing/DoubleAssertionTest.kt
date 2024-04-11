package com.ivy.data.model.testing

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DoubleAssertionTest {

    enum class DoubleApproxTestCase(
        val a: Double,
        val b: Double,
        val approxTheSame: Boolean
    ) {
        Exactly(
            a = 3.14,
            b = 3.14,
            approxTheSame = true
        ),
        ApproxPI(
            a = 3.14,
            b = 3.139999,
            approxTheSame = true
        ),
        NotApproxPI(
            a = 3.14,
            b = 3.135,
            approxTheSame = false
        ),
        RealMoney(
            a = 100.0,
            b = 99.99,
            approxTheSame = false
        ),
        RealMoneyBig(
            a = 1_000_000.0,
            b = 999_999.0,
            approxTheSame = false
        )
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
}