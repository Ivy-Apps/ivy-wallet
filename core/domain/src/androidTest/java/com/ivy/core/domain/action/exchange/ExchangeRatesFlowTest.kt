package com.ivy.core.domain.action.exchange

import com.ivy.core.domain.action.settings.basecurrency.WriteBaseCurrencyAct
import com.ivy.data.exchange.ExchangeRatesData
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import javax.inject.Inject

@HiltAndroidTest
class ExchangeRatesFlowTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var exchangeRatesFlow: ExchangeRatesFlow

    @Inject
    lateinit var writeBaseCurrencyAct: WriteBaseCurrencyAct

    @Inject
    lateinit var syncExchangeRatesAct: SyncExchangeRatesAct

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        hiltRule.inject()
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Ignore("integration tests are broken")
    @Test
    fun fetches_exchange_rates_for_usd(): Unit = runBlocking {
        // Arrange
        writeBaseCurrencyAct("USD")
        syncExchangeRatesAct("USD")

        // Act
        val res = exchangeRatesFlow().take(2).toList()

        // Assert
        res.first() shouldBe ExchangeRatesData(
            baseCurrency = "", rates = emptyMap()
        )
        res[1].baseCurrency shouldBe "USD"
        res[1].rates.size shouldBeGreaterThan 0
        println("rates = ${res[1].rates}")
    }
}