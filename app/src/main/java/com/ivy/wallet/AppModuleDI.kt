package com.ivy.wallet

import android.content.Context
import com.ivy.billing.IvyBilling
import com.ivy.notifications.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModuleDI {
    @Provides
    @Singleton
    fun provideIvyBilling(
    ): IvyBilling {
        return IvyBilling()
    }

    @Provides
    fun provideNotificationService(
        @ApplicationContext appContext: Context
    ): NotificationService {
        return NotificationService(appContext)
    }
}