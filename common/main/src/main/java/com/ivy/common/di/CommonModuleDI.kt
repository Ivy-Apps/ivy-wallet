package com.ivy.common.di

import com.ivy.common.time.provider.DeviceTimeProvider
import com.ivy.common.time.provider.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModuleDI {
    @Singleton
    @Binds
    abstract fun timeProvider(provider: DeviceTimeProvider): TimeProvider
}