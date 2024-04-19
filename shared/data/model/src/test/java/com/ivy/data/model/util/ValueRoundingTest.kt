package com.ivy.data.model.util

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.data.model.PositiveValue
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ValueRoundingTest {
    enum class RoundingTestCase(
        val double: Double,
        val expectedRounded: Double,
    ) {
        Pi(
            double = 3.14159265359,
            expectedRounded = 3.14,
        ),
        RoundHalfUp(
            double = 0.005,
            expectedRounded = 0.01,
        ),
        RoundUp(
            double = 0.006,
            expectedRounded = 0.01,
        ),
        RoundDown(
            double = 1.004,
            expectedRounded = 1.00,
        ),
        AnotherExample(
            double = 1.996,
            expectedRounded = 2.00,
        ),
    }

    @Test
    fun `rounds value correctly`(
        @TestParameter testCase: RoundingTestCase
    ) {
        // given
        val value = PositiveValue(
            amount = PositiveDouble.unsafe(testCase.double),
            asset = AssetCode.EUR
        )

        // when
        val roundedValue = value.round(decimalPlaces = 2)

        // then
        roundedValue.amount.value shouldBe testCase.expectedRounded
        roundedValue.asset shouldBe AssetCode.EUR
    }
}