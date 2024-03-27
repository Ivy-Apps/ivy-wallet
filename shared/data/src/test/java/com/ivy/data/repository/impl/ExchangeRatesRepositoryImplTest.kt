package com.ivy.data.repository.impl

import arrow.core.left
import arrow.core.right
import com.ivy.base.TestDispatchersProvider
import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.write.WriteExchangeRatesDao
import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.remote.RemoteExchangeRatesDataSource
import com.ivy.data.remote.responses.ExchangeRatesResponse
import com.ivy.data.repository.ExchangeRatesRepository
import com.ivy.data.repository.mapper.ExchangeRateMapper
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ExchangeRatesRepositoryImplTest {
    private val mapper = mockk<ExchangeRateMapper>()
    private val exchangeRatesDao = mockk<ExchangeRatesDao>()
    private val writeExchangeRatesDao = mockk<WriteExchangeRatesDao>()
    private val remoteExchangeRatesDataSource = mockk<RemoteExchangeRatesDataSource>()

    private lateinit var repository: ExchangeRatesRepository

    @Before
    fun setup() {
        repository = ExchangeRatesRepositoryImpl(
            mapper = mapper,
            exchangeRatesDao = exchangeRatesDao,
            writeExchangeRatesDao = writeExchangeRatesDao,
            remoteExchangeRatesDataSource = remoteExchangeRatesDataSource,
            dispatchersProvider = TestDispatchersProvider,
        )
    }

    @Test
    fun `fetchExchangeRates - successful network responses`() = runTest {
        // given
        val mockResponse = ExchangeRatesResponse(
            date = "",
            rates = emptyMap(),
        )
        coEvery {
            remoteExchangeRatesDataSource.fetchEurExchangeRates()
        } returns mockResponse.right()

        // when
        val result = repository.fetchExchangeRates()

        // then
        result shouldBe mockResponse
    }

    @Test
    fun `fetchExchangeRates - unsuccessful network responses`() = runTest {
        // given
        val mockResponse = "Network Error"

        coEvery {
            remoteExchangeRatesDataSource.fetchEurExchangeRates()
        } returns mockResponse.left()

        // when
        val result = repository.fetchExchangeRates()

        // then
        result shouldBe null
    }

    @Test
    fun `findByBaseCurrencyAndCurrency - exchange rate is found and mapped`() = runTest {
        // given
        val mockEntity = ExchangeRateEntity("usd", "aed", 2.0)
        val mockDomain = ExchangeRate(
            baseCurrency = AssetCode.unsafe("usd"),
            currency = AssetCode.unsafe("aed"),
            rate = PositiveDouble.unsafe(2.0),
            manualOverride = false
        )

        coEvery {
            exchangeRatesDao.findByBaseCurrencyAndCurrency("usd", "aed")
        } returns mockEntity
        every {
            with(mapper) { mockEntity.toDomain() }
        } returns mockDomain.right()

        // when
        val result = repository.findByBaseCurrencyAndCurrency("usd", "aed")

        // then
        result shouldBe mockDomain
    }

    @Test
    fun `findByBaseCurrencyAndCurrency - exchange rate is not found`() = runTest {
        // given
        coEvery {
            exchangeRatesDao.findByBaseCurrencyAndCurrency("usd", "aed")
        } returns null

        // when
        val result = repository.findByBaseCurrencyAndCurrency("usd", "aed")

        // then
        result shouldBe null
    }
}
