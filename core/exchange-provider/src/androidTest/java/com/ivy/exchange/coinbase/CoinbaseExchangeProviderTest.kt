package com.ivy.exchange.coinbase

import com.ivy.common.test.AndroidTest
import com.ivy.data.exchange.ExchangeProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@AndroidTest
@HiltAndroidTest
class CoinbaseExchangeProviderTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    @Inject
    lateinit var sut: CoinbaseExchangeProvider

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun fetch_exchange_rates_successfully(): Unit = runBlocking {
        val res = sut.fetchExchangeRates(baseCurrency = "USD")

        res.provider shouldBe ExchangeProvider.Coinbase
        res.ratesMap.size shouldBeGreaterThan 1
        println("rates: ${res.ratesMap}")
    }

}