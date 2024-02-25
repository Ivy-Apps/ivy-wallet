package com.ivy.base.di

import android.content.Context
import com.ivy.base.Toaster
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedModule {

    @Singleton
    @Provides
    fun provideToaster(@ApplicationContext context: Context): Toaster {
        return Toaster(context)
    }
}