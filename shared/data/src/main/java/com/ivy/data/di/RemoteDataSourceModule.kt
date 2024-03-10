package com.ivy.data.di

import com.ivy.data.remote.RemoteExchangeRatesDataSource
import com.ivy.data.remote.impl.RemoteExchangeRatesDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {

    @Provides
    @Singleton
    fun provideRemoteExchangeRatesDataSource(
        ktorClient : HttpClient
    ): RemoteExchangeRatesDataSource {
        return RemoteExchangeRatesDataSourceImpl(lazy { ktorClient })
    }
}