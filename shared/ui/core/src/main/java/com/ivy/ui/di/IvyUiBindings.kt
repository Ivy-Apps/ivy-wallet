package com.ivy.ui.di

import android.content.Context
import com.ivy.domain.features.IvyFeatures
import com.ivy.ui.FormatMoneyUseCase
import com.ivy.ui.time.DevicePreferences
import com.ivy.ui.time.TimeFormatter
import com.ivy.ui.time.impl.AndroidDateTimePicker
import com.ivy.ui.time.impl.AndroidDevicePreferences
import com.ivy.ui.time.impl.DateTimePicker
import com.ivy.ui.time.impl.IvyTimeFormatter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface IvyUiBindings {
    @Binds
    fun timeFormatter(impl: IvyTimeFormatter): TimeFormatter

    @Binds
    fun deviceTimePreferences(impl: AndroidDevicePreferences): DevicePreferences

    @Binds
    fun dateTimePicker(impl: AndroidDateTimePicker): DateTimePicker

    companion object {
        @Singleton
        @Provides
        fun formatMoneyUseCase(
            features: IvyFeatures,
            devicePreferences: DevicePreferences,
            @ApplicationContext context: Context
        ): FormatMoneyUseCase = FormatMoneyUseCase(features, devicePreferences, context)
    }
}