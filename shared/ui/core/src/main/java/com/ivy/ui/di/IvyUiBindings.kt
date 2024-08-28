package com.ivy.ui.di

import com.ivy.ui.time.IvyTimeFormatter
import com.ivy.ui.time.TimeFormatter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface IvyUiBindings {
    @Binds
    fun timeFormatter(impl: IvyTimeFormatter): TimeFormatter
}