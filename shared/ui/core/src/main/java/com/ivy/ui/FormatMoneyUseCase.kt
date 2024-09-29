package com.ivy.ui

import androidx.compose.runtime.Composable
import com.ivy.domain.features.Features
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

class FormatMoneyUseCase @Inject constructor(
    private val feature: Features
) {
    private val decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
    private val formatter = DecimalFormat("#,##,##0", decimalFormatSymbols)

    @Composable
    fun format(value: Double): String {
        val showDecimal = feature.showDecimalNumber.asEnabledState()
        return when (showDecimal) {
            true -> value.toString()
            else -> formatter.format(value)
        }
    }
}