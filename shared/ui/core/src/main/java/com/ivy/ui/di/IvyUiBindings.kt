package com.ivy.ui.di

import com.ivy.ui.time.DeviceTimePreferences
import com.ivy.ui.time.TimeFormatter
import com.ivy.ui.time.impl.AndroidDeviceTimePreferences
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
    fun deviceTimePreferences(impl: AndroidDeviceTimePreferences): DeviceTimePreferences
}