package com.ivy.exchange.di

import com.ivy.exchange.RemoteExchangeProvider
import com.ivy.exchange.coinbase.CoinbaseExchangeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExchangeBindingDI {
    @Binds
    abstract fun exchangeProvider(coinbase: CoinbaseExchangeProvider): RemoteExchangeProvider
}