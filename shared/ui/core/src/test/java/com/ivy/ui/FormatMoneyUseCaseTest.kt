package com.ivy.ui

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.ivy.domain.features.BoolFeature
import com.ivy.domain.features.FeatureGroup
import com.ivy.domain.features.Features
import com.ivy.ui.time.DevicePreferences
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import java.util.Locale

@RunWith(ParameterizedRobolectricTestRunner::class)
class FormatMoneyUseCaseTest(private val param: TestData) {

    private val context: Context = InstrumentationRegistry.getInstrumentation().context
    private val features = mockk<Features>()
    private val devicePreferences = mockk<DevicePreferences>()

    class TestData(
        val givenInput: Double,
        val showDecimal: BoolFeature,
        val locale: Locale,
        val expectedOutPut: String
    )

    private lateinit var formatMoneyUseCase: FormatMoneyUseCase

    @Test
    fun `validate decimal formatting`(): Unit = runBlocking {
        // given
        every { features.showDecimalNumber } returns param.showDecimal
        every { devicePreferences.locale() } returns param.locale
        formatMoneyUseCase = FormatMoneyUseCase(features, devicePreferences, context)

        // when
        val result = formatMoneyUseCase.format(value = param.givenInput)

        // then
        result shouldBe param.expectedOutPut
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "Input: {0}")
        fun params() = listOf(
            TestData(
                givenInput = 1000.12,
                showDecimal = BoolFeature(
                    key = "show_decimal_number",
                    group = FeatureGroup.Other,
                    name = "Show Decimal Number",
                    description = "Whether to show the decimal part in amounts",
                    defaultValue = true
                ),
                locale = Locale.ENGLISH,
                expectedOutPut = "1,000.12"
            ),
            TestData(
                givenInput = 1000.12,
                showDecimal = BoolFeature(
                    key = "show_decimal_number",
                    group = FeatureGroup.Other,
                    name = "Show Decimal Number",
                    description = "Whether to show the decimal part in amounts",
                    defaultValue = false
                ),
                locale = Locale.ENGLISH,
                expectedOutPut = "1,000"
            ),
            TestData(
                givenInput = 1000.12,
                showDecimal = BoolFeature(
                    key = "show_decimal_number",
                    group = FeatureGroup.Other,
                    name = "Show Decimal Number",
                    description = "Whether to show the decimal part in amounts",
                    defaultValue = true
                ),
                locale = Locale.GERMAN,
                expectedOutPut = "1.000,12"
            ),
            TestData(
                givenInput = 1000.12,
                showDecimal = BoolFeature(
                    key = "show_decimal_number",
                    group = FeatureGroup.Other,
                    name = "Show Decimal Number",
                    description = "Whether to show the decimal part in amounts",
                    defaultValue = false
                ),
                locale = Locale.GERMAN,
                expectedOutPut = "1.000"
            ),
        )
    }
}