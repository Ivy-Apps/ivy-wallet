package com.ivy.ui

import android.content.Context
import com.ivy.domain.features.Features
import com.ivy.ui.time.DevicePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import javax.inject.Inject

const val MILLION = 1_000_000
const val BILLION = 1_000_000_000

class FormatMoneyUseCase @Inject constructor(
    private val features: Features,
    private val devicePreferences: DevicePreferences,
    @ApplicationContext private val context: Context
) {

    private val locale = devicePreferences.locale()
    private val withoutDecimalFormatter = DecimalFormat("###,###", DecimalFormatSymbols(locale))
    private val withDecimalFormatter = DecimalFormat("###,###.00", DecimalFormatSymbols(locale))

    suspend fun format(value: Double): String {
        when (value >= MILLION) {
            true -> {
                val result = if (value >= BILLION) {
                    String.format(locale, "%.2fB", value / BILLION)
                } else {
                    String.format(locale, "%.2fM", value / MILLION)
                }
                return result
            }

            else -> {
                val showDecimalPoint = features.showDecimalNumber.isEnabled(context)

                return when (showDecimalPoint) {
                    true -> withDecimalFormatter.format(value)
                    false -> withoutDecimalFormatter.format(value)
                }
            }
        }
    }
}