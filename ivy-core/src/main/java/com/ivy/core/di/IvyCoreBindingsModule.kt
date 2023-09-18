package com.ivy.core.di

import com.ivy.core.features.Features
import com.ivy.core.features.IvyFeatures
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IvyCoreBindingsModule {
    @Binds
    abstract fun bindFeatures(features: IvyFeatures): Features
}