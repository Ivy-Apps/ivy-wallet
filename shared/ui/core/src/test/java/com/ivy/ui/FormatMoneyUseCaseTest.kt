package com.ivy.ui

import androidx.compose.runtime.Composable
import com.ivy.domain.features.BoolFeature
import com.ivy.domain.features.FeatureGroup
import com.ivy.domain.features.Features
import com.ivy.ui.time.DevicePreferences
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.util.Locale

class FormatMoneyUseCaseTest {

    private val features = mockk<Features>()
    private val devicePreferences = mockk<DevicePreferences>()
    private val showDecimal = BoolFeature(
        key = "show_decimal_number",
        group = FeatureGroup.Other,
        name = "Show Decimal Number",
        description = "Show Decimal Number in amounts",
        defaultValue = true
    )

    private val hideDecimal = BoolFeature(
        key = "show_decimal_number",
        group = FeatureGroup.Other,
        name = "Show Decimal Number",
        description = "Show Decimal Number in amounts",
        defaultValue = false
    )

    private lateinit var formatMoneyUseCase: FormatMoneyUseCase

    @Before
    fun setup() {
        formatMoneyUseCase = FormatMoneyUseCase(features,devicePreferences)
    }

    @Composable
    @Test
    fun `Format with no decimal place locale ENGLISH`() {
        //given
        every { features.showDecimalNumber } returns hideDecimal
        every { devicePreferences.locale() } returns Locale.ENGLISH

        val result = formatMoneyUseCase.format(value = 1000.12)
        result shouldBe "1,000"
    }
}