package com.ivy.base.di

import com.ivy.base.threading.DispatchersProvider
import com.ivy.base.threading.IvyDispatchersProvider
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.base.time.impl.DeviceTimeProvider
import com.ivy.base.time.impl.StandardTimeConvert
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BaseHiltBindings {
    @Binds
    fun dispatchersProvider(impl: IvyDispatchersProvider): DispatchersProvider

    @Binds
    fun bindTimezoneProvider(impl: DeviceTimeProvider): TimeProvider

    @Binds
    fun bindTimeConverter(impl: StandardTimeConvert): TimeConverter
}