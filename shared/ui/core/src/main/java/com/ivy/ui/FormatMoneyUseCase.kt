package com.ivy.ui

import android.content.Context
import com.ivy.domain.features.Features
import com.ivy.ui.time.DevicePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import javax.inject.Inject
import kotlin.math.abs

const val MILLION = 1_000_000
const val BILLION = 1_000_000_000
const val HUNDREDOFTHOUSAND = 100000

class FormatMoneyUseCase @Inject constructor(
    private val features: Features,
    private val devicePreferences: DevicePreferences,
    @ApplicationContext private val context: Context
) {

    private val locale = devicePreferences.locale()
    private val withoutDecimalFormatter = DecimalFormat("###,###", DecimalFormatSymbols(locale))
    private val withDecimalFormatter = DecimalFormat("###,###.00", DecimalFormatSymbols(locale))

    suspend fun format(value: Double, shortenAmount: Boolean): String {
        when (abs(value) >= HUNDREDOFTHOUSAND && shortenAmount) {
            true -> {
                val result = if (abs(value) >= BILLION) {
                    String.format(locale, "%.2fb", value / BILLION)
                } else if (abs(value) >= MILLION) {
                    String.format(locale, "%.2fm", value / MILLION)
                } else {
                    String.format(locale, "%.2fk", value / HUNDREDOFTHOUSAND)
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