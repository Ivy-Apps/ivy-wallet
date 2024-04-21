package com.ivy.domain.usecase.stat

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.data.model.PositiveValue
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonNegativeInt
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.domain.model.StatSummary
import com.ivy.domain.usecase.StatSummaryBuilder
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class StatSummaryBuilderTest {

    enum class ValuesTestCase(
        val values: List<PositiveValue>,
        val expected: StatSummary,
    ) {
        Empty(
            values = emptyList(),
            expected = StatSummary.Zero
        ),
        One(
            values = listOf(
                value(1.0, AssetCode.EUR)
            ),
            expected = StatSummary(
                trnCount = count(1),
                values = mapOf(AssetCode.EUR to amount(1.0))
            )
        ),
        TwoInDiffCurrency(
            values = listOf(
                value(3.14, AssetCode.EUR),
                value(42.0, AssetCode.USD),
            ),
            expected = StatSummary(
                trnCount = count(2),
                values = mapOf(
                    AssetCode.EUR to amount(3.14),
                    AssetCode.USD to amount(42.0)
                )
            )
        ),
        TwoInSameCurrency(
            values = listOf(
                value(6.0, AssetCode.EUR),
                value(4.0, AssetCode.EUR),
            ),
            expected = StatSummary(
                trnCount = count(2),
                values = mapOf(
                    AssetCode.EUR to amount(10.0),
                )
            )
        ),
        TwoInSameCurrencyAndInDiffCurrency(
            values = listOf(
                value(6.0, AssetCode.EUR),
                value(4.0, AssetCode.EUR),
                value(50.0, AssetCode.USD),
            ),
            expected = StatSummary(
                trnCount = count(3),
                values = mapOf(
                    AssetCode.EUR to amount(10.0),
                    AssetCode.USD to amount(50.0),
                )
            )
        ),
        ThreeInSameCurrency(
            values = listOf(
                value(6.0, AssetCode.EUR),
                value(4.0, AssetCode.EUR),
                value(0.5, AssetCode.EUR),
            ),
            expected = StatSummary(
                trnCount = count(3),
                values = mapOf(
                    AssetCode.EUR to amount(10.5),
                )
            )
        ),
        ThreeInDiffCurrency(
            values = listOf(
                value(6.0, AssetCode.EUR),
                value(4.0, AssetCode.USD),
                value(0.5, AssetCode.GBP),
            ),
            expected = StatSummary(
                trnCount = count(3),
                values = mapOf(
                    AssetCode.EUR to amount(6.0),
                    AssetCode.USD to amount(4.0),
                    AssetCode.GBP to amount(0.5),
                )
            )
        ),
    }

    @Test
    fun `builds stats summary`(
        @TestParameter testCase: ValuesTestCase
    ) {
        // given
        val statSummaryBuilder = StatSummaryBuilder()

        // when
        testCase.values.forEach(statSummaryBuilder::process)
        val statSummary = statSummaryBuilder.build()

        // then
        statSummary shouldBe testCase.expected
    }

    @Test
    fun `handles 1x double overflow`() {
        // given
        val statSummaryBuilder = StatSummaryBuilder()

        // when
        statSummaryBuilder.process(value(3.14, AssetCode.EUR))
        statSummaryBuilder.process(value(Double.MAX_VALUE, AssetCode.EUR))
        val statSummary = statSummaryBuilder.build()

        // then
        statSummary shouldBe StatSummary(
            trnCount = count(2),
            values = mapOf(
                AssetCode.EUR to amount(Double.MAX_VALUE)
            )
        )
    }

    @Test
    fun `handles 2x double overflow`() {
        // given
        val statSummaryBuilder = StatSummaryBuilder()

        // when
        statSummaryBuilder.process(value(3.14, AssetCode.EUR))
        statSummaryBuilder.process(value(Double.MAX_VALUE, AssetCode.EUR))
        statSummaryBuilder.process(value(Double.MAX_VALUE, AssetCode.EUR))
        val statSummary = statSummaryBuilder.build()

        // then
        statSummary shouldBe StatSummary(
            trnCount = count(3),
            values = mapOf(
                AssetCode.EUR to amount(Double.MAX_VALUE)
            )
        )
    }

    companion object {
        private fun value(
            amount: Double,
            asset: AssetCode
        ): PositiveValue = PositiveValue(PositiveDouble.unsafe(amount), asset)

        private fun count(count: Int): NonNegativeInt = NonNegativeInt.unsafe(count)

        private fun amount(amount: Double): PositiveDouble = PositiveDouble.unsafe(amount)
    }
}