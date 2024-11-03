package com.ivy.ui

import android.content.Context
import com.ivy.domain.features.Features
import com.ivy.ui.time.DevicePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import javax.inject.Inject
import kotlin.math.abs

const val THOUSAND = 1_000
const val MILLION = 1_000_000
const val BILLION = 1_000_000_000

/**
 * A use case class responsible for formatting currency and cryptocurrency values based on user preferences.
 * It supports regular currency formatting with or without decimal places, as well as shortened formats
 * (e.g., "1k", "1m"). For cryptocurrency, it formats up to 9 decimal places and removes unnecessary trailing zeros.
 *
 * @property features Provides feature toggles to customize app behavior.
 * @property devicePreferences Manages user-specific preferences for locale and other device settings.
 * @property context Application context, used for feature check and resource access.
 */
class FormatMoneyUseCase @Inject constructor(
    private val features: Features,
    private val devicePreferences: DevicePreferences,
    @ApplicationContext private val context: Context
) {

    private val locale = devicePreferences.locale()
    private val withoutDecimalFormatter = DecimalFormat("###,###", DecimalFormatSymbols(locale))
    private val withDecimalFormatter = DecimalFormat("###,###.00", DecimalFormatSymbols(locale))
    private val shortenAmountFormatter = DecimalFormat("###,###.##", DecimalFormatSymbols(locale))
    private val cryptoFormatter = DecimalFormat("###,###." + "#".repeat(9), DecimalFormatSymbols(locale))

    /**
     * Formats a currency or cryptocurrency amount based on the input parameters.
     *
     * @param value The numeric value to format.
     * @param shortenAmount Flag to indicate if the amount should be shortened (e.g., "1k" for 1,000).
     * @param isCrypto Flag to indicate if the value is a cryptocurrency, enabling up to 9 decimal places.
     * @return The formatted string representation of the value.
     */
    suspend fun format(value: Double, shortenAmount: Boolean, isCrypto: Boolean = false): String {
        return if (isCrypto) {
            cryptoFormatter.format(value)
        } else if (abs(value) >= THOUSAND && shortenAmount) {
            if (abs(value) >= BILLION) {
                "${shortenAmountFormatter.format(value / BILLION)}b"
            } else if (abs(value) >= MILLION) {
                "${shortenAmountFormatter.format(value / MILLION)}m"
            } else {
                "${shortenAmountFormatter.format(value / THOUSAND)}k"
            }
        } else {
            val showDecimalPoint = features.showDecimalNumber.isEnabled(context)

            val formatter = when (showDecimalPoint) {
                true -> withDecimalFormatter
                false -> withoutDecimalFormatter
            }
            formatter.format(value)
        }
    }
}
