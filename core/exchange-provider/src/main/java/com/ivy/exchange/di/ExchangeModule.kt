package com.ivy.exchange.di

import com.ivy.exchange.ExchangeProvider
import com.ivy.exchange.coinbase.CoinbaseExchangeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExchangeModule {
    @Binds
    abstract fun exchangeProvider(coinbase: CoinbaseExchangeProvider): ExchangeProvider
}