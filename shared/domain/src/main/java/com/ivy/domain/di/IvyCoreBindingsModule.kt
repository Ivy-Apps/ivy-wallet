package com.ivy.domain.di

import com.ivy.domain.features.Features
import com.ivy.domain.features.IvyFeatures
import com.ivy.domain.time.TimeConverter
import com.ivy.domain.time.TimeZoneProvider
import com.ivy.domain.time.impl.DeviceTimeZoneProvider
import com.ivy.domain.time.impl.StandardTimeConvert
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface IvyCoreBindingsModule {
    @Binds
    fun bindFeatures(features: IvyFeatures): Features

    @Binds
    fun bindTimezoneProvider(impl: DeviceTimeZoneProvider): TimeZoneProvider

    @Binds
    fun bindTimeConverter(impl: StandardTimeConvert): TimeConverter
}
