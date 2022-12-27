package com.ivy.exchange.di

import com.ivy.exchange.RemoteExchangeProvider
import com.ivy.exchange.fawazahmed0.Fawazahmed0ExchangeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExchangeModuleDI {
    @Binds
    abstract fun exchangeProvider(provider: Fawazahmed0ExchangeProvider): RemoteExchangeProvider
}