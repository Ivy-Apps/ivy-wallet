package com.ivy.base.di

import com.ivy.base.threading.DispatchersProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object BaseModule {
    @AppCoroutineScope
    @Provides
    fun provideApplicationCoroutineScope(
        dispatchers: DispatchersProvider
    ): CoroutineScope {
        val applicationJob = SupervisorJob()
        return CoroutineScope(dispatchers.main + applicationJob)
    }
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class AppCoroutineScope