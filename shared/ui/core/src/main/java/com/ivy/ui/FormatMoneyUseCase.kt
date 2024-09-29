package com.ivy.ui

import androidx.compose.runtime.Composable
import com.ivy.domain.features.Features
import com.ivy.ui.time.DevicePreferences
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

class FormatMoneyUseCase @Inject constructor(
    private val feature: Features,
    private val devicePreferences: DevicePreferences,
) {
    private val locale = devicePreferences.locale()
    private val formatterWithoutDecimal = DecimalFormat("###,###", DecimalFormatSymbols(locale))
    private val formatterWithDecimal = DecimalFormat("###,###.00", DecimalFormatSymbols(locale))
    @Composable
    fun format(value: Double): String {
        val showDecimal = feature.showDecimalNumber.asEnabledState()
        return when (showDecimal) {
            true -> formatterWithDecimal.format(value)
            else -> formatterWithoutDecimal.format(value)
        }
    }
}