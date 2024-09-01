package com.ivy.ui.di

import com.ivy.ui.time.DevicePreferences
import com.ivy.ui.time.TimeFormatter
import com.ivy.ui.time.impl.AndroidDateTimePicker
import com.ivy.ui.time.impl.AndroidDevicePreferences
import com.ivy.ui.time.impl.DateTimePicker
import com.ivy.ui.time.impl.IvyTimeFormatter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface IvyUiBindings {
    @Binds
    fun timeFormatter(impl: IvyTimeFormatter): TimeFormatter

    @Binds
    fun deviceTimePreferences(impl: AndroidDevicePreferences): DevicePreferences

    @Binds
    fun dateTimePicker(impl: AndroidDateTimePicker): DateTimePicker
}