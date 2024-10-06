package com.ivy.ui

import android.content.Context
import com.ivy.domain.features.Features
import com.ivy.ui.time.DevicePreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormatMoneyUseCaseImpl @Inject constructor(
    private val features: Features,
    private val devicePreferences: DevicePreferences,
    @ApplicationContext private val context: Context
) : FormatMoneyUseCase {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FormatMoneyUseCaseEntryPoint {
        @Singleton
        fun FormatMoneyUseCase(): FormatMoneyUseCase
    }

    private val locale = devicePreferences.locale()
    private val withoutDecimalFormatter = DecimalFormat("###,###", DecimalFormatSymbols(locale))
    private val withDecimalFormatter = DecimalFormat("###,##0.00", DecimalFormatSymbols(locale))

    override suspend fun format(value: Double): String {
        val showDecimalPoint = features.showDecimalNumber.isEnabled(context)

        return when (showDecimalPoint) {
            true -> withDecimalFormatter.format(value)
            false -> withoutDecimalFormatter.format(value)
        }
    }
}

interface FormatMoneyUseCase {
    suspend fun format(value: Double): String
}
