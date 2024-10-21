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
        val shortenAmount: Boolean,
        val locale: Locale,
        val expectedOutput: String
    ) {
        ENG_SHOW_DECIMAL(
            amount = 1_000.12,
            showDecimal = true,
            shortenAmount = false,
            locale = Locale.ENGLISH,
            expectedOutput = "1,000.12"
        ),
        ENG_HIDE_DECIMAL(
            amount = 1_000.12,
            showDecimal = false,
            shortenAmount = false,
            locale = Locale.ENGLISH,
            expectedOutput = "1,000"
        ),
        GERMAN_SHOW_DECIMAL(
            amount = 1_000.12,
            showDecimal = true,
            shortenAmount = false,
            locale = Locale.GERMAN,
            expectedOutput = "1.000,12"
        ),
        GERMAN_HIDE_DECIMAL(
            amount = 1_000.12,
            showDecimal = false,
            shortenAmount = false,
            locale = Locale.GERMAN,
            expectedOutput = "1.000"
        ),
        ENGLISH_1K_SHORT_AMT(
            amount = 13_000.10,
            showDecimal = true,
            shortenAmount = true,
            locale = Locale.ENGLISH,
            expectedOutput = "13k"
        ),
        ENGLISH_MILLION_SHORT_AMT(
            amount = 1_233_500.10,
            showDecimal = true,
            shortenAmount = true,
            locale = Locale.ENGLISH,
            expectedOutput = "1.23m"
        ),
        ENGLISH_BILLION_SHORT_AMT(
            amount = 1_233_000_000.10,
            showDecimal = true,
            shortenAmount = true,
            locale = Locale.ENGLISH,
            expectedOutput = "1.23b"
        ),
        GERMAN_1K_SHORT_AMT(
            amount = 13_000.10,
            showDecimal = true,
            shortenAmount = true,
            locale = Locale.GERMAN,
            expectedOutput = "13k"
        ),
        GERMAN_MILLION_SHORT_AMT(
            amount = 1_233_500.10,
            showDecimal = true,
            shortenAmount = true,
            locale = Locale.GERMAN,
            expectedOutput = "1,23m"
        ),
        GERMAN_BILLION_SHORT_AMT(
            amount = 1_233_000_000.10,
            showDecimal = true,
            shortenAmount = true,
            locale = Locale.GERMAN,
            expectedOutput = "1,23b"
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
        val result = formatMoneyUseCase.format(
            value = testCase.amount,
            shortenAmount = testCase.shortenAmount
        )

        // then
        result shouldBe testCase.expectedOutput
    }
}