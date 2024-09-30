package com.ivy.ui

import android.content.Context
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.domain.features.Features
import com.ivy.ui.time.DevicePreferences
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(TestParameterInjector::class)
class FormatMoneyUseCaseTest {

    private val features = mockk<Features>()
    private val devicePreferences = mockk<DevicePreferences>()

    enum class MoneyFormatterTestCase(
        val amount: Double,
        val showDecimal: Boolean,
        val locale: Locale,
        val expectedOutput: String
    ) {
        ENG_SHOW_DECIMAL(
            amount = 1000.12,
            showDecimal = true,
            locale = Locale.ENGLISH,
            expectedOutput = "1,000.12"
        ),
        ENG_HIDE_DECIMAL(
            amount = 1000.12,
            showDecimal = false,
            locale = Locale.ENGLISH,
            expectedOutput = "1,000"
        ),
        GERMAN_SHOW_DECIMAL(
            amount = 1000.12,
            showDecimal = true,
            locale = Locale.GERMAN,
            expectedOutput = "1.000,12"
        ),
        GERMAN_HIDE_DECIMAL(
            amount = 1000.12,
            showDecimal = false,
            locale = Locale.GERMAN,
            expectedOutput = "1.000"
        ),
    }

    private lateinit var formatMoneyUseCase: FormatMoneyUseCase

    @Test
    fun `validate decimal formatting`(
        @TestParameter testCase: MoneyFormatterTestCase
    ): Unit = runBlocking {
        // given
        val context = mockk<Context>()
        every { features.showDecimalNumber } returns mockk { coEvery { isEnabled(any()) } returns testCase.showDecimal }
        every { devicePreferences.locale() } returns testCase.locale
        formatMoneyUseCase = FormatMoneyUseCase(features, devicePreferences, context)

        // when
        val result = formatMoneyUseCase.format(value = testCase.amount)

        // then
        result shouldBe testCase.expectedOutput
    }
}