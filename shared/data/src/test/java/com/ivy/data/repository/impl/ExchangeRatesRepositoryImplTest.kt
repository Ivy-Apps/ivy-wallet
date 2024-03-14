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
import com.ivy.data.repository.mapper.ExchangeRateMapper
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow

class ExchangeRatesRepositoryImplTest : FreeSpec({
    val mapper = mockk<ExchangeRateMapper>()
    val exchangeRatesDao = mockk<ExchangeRatesDao>()
    val writeExchangeRatesDao = mockk<WriteExchangeRatesDao>()
    val remoteExchangeRatesDataSource = mockk<RemoteExchangeRatesDataSource>()

    fun newRepository() =
        ExchangeRatesRepositoryImpl(
            mapper = mapper,
            exchangeRatesDao = exchangeRatesDao,
            writeExchangeRatesDao = writeExchangeRatesDao,
            remoteExchangeRatesDataSource = remoteExchangeRatesDataSource,
            dispatchersProvider = TestDispatchersProvider,
        )

    "fetchExchangeRates" - {
        "successful network responses" {
            // given
            val repository = newRepository()
            val urls = repository.urls
            val mockResponse =
                ExchangeRatesResponse(
                    date = "",
                    rates = emptyMap(),
                )

            urls.forEach { url ->
                coEvery {
                    remoteExchangeRatesDataSource.fetchEurExchangeRates(url)
                } returns mockResponse.right()
            }

            // when
            val result = repository.fetchExchangeRates()

            // then
            result shouldBe mockResponse
        }

        "unsuccessful network responses" {
            // given
            val repository = newRepository()
            val urls = repository.urls
            val mockResponse = "Network Error"

            urls.forEach { url ->
                coEvery {
                    remoteExchangeRatesDataSource.fetchEurExchangeRates(url)
                } returns mockResponse.left()
            }

            // when
            val result = repository.fetchExchangeRates()

            // then
            result shouldBe null
        }
    }

    "findByBaseCurrencyAndCurrency" - {
        "exchange rate is found and mapped" {
            // given
            val repository = newRepository()

            val mockEntity = ExchangeRateEntity("", "", 0.0)
            val mockDomain = ExchangeRate(AssetCode(""), "", PositiveDouble(0.0), false)

            coEvery {
                exchangeRatesDao.findByBaseCurrencyAndCurrency("", "")
            } returns mockEntity
            every {
                with(mapper) { mockEntity.toDomain() }
            } returns mockDomain

            // when
            val result = repository.findByBaseCurrencyAndCurrency("", "")

            // then
            result shouldBe mockDomain
        }

        "exchange rate is not found" {
            // given
            val repository = newRepository()

            coEvery {
                exchangeRatesDao.findByBaseCurrencyAndCurrency("", "")
            } returns null

            // when
            val result = repository.findByBaseCurrencyAndCurrency("", "")

            // then
            result shouldBe null
        }
    }

    "save" - {
        "entity is saved" {
            // given
            val repository = newRepository()
            val mockEntity = ExchangeRateEntity("", "", 0.0)

            coEvery { writeExchangeRatesDao.save(mockEntity) } returns Unit

            // when
            repository.save(mockEntity)

            // then
            coVerify { writeExchangeRatesDao.save(mockEntity) }
        }

        "rate is mapped and saved" {
            // given
            val repository = newRepository()
            val mockRate = ExchangeRate(AssetCode(""), "", PositiveDouble(0.0), false)
            val mockEntity = ExchangeRateEntity("", "", 0.0)

            coEvery { writeExchangeRatesDao.save(mockEntity) } returns Unit
            every { with(mapper) { mockRate.toEntity() } } returns mockEntity

            // when
            repository.save(mockRate)

            // then
            coVerify {
                writeExchangeRatesDao.save(mockEntity)
            }
        }
    }

    "saveManyEntities" - {
        "entities are saved" {
            // given
            val repository = newRepository()
            val mockEntities =
                listOf(
                    ExchangeRateEntity("", "", 0.0),
                    ExchangeRateEntity("", "", 0.0),
                    ExchangeRateEntity("", "", 0.0),
                )

            coEvery { writeExchangeRatesDao.saveMany(mockEntities) } returns Unit

            // when
            repository.saveManyEntities(mockEntities)

            // then
            coVerify { writeExchangeRatesDao.saveMany(mockEntities) }
        }
    }

    "saveManyRates" - {
        "rates are mapped and saved" {
            // given
            val repository = newRepository()
            val mockRates =
                listOf(
                    ExchangeRate(AssetCode(""), "", PositiveDouble(0.0), false),
                    ExchangeRate(AssetCode(""), "", PositiveDouble(0.0), false),
                    ExchangeRate(AssetCode(""), "", PositiveDouble(0.0), false),
                )
            val mockEntities =
                listOf(
                    ExchangeRateEntity("", "", 0.0, false),
                    ExchangeRateEntity("", "", 0.0, false),
                    ExchangeRateEntity("", "", 0.0, false),
                )

            coEvery { writeExchangeRatesDao.saveMany(mockEntities) } returns Unit

            // when
            repository.saveManyRates(mockRates)

            // then
            verify {
                with(mapper) { mockRates.map { it.toEntity() } }
            }
            coVerify {
                writeExchangeRatesDao.saveMany(mockEntities)
            }
        }
    }

    "deleteAll" - {
        "exchange rates are deleted" {
            // given
            val repository = newRepository()

            coEvery { writeExchangeRatesDao.deleteALl() } returns Unit

            // when
            repository.deleteAll()

            // then
            coVerify(exactly = 1) { writeExchangeRatesDao.deleteALl() }
        }
    }

    "findAll" - {
        "empty list" {
            // given
            val repository = newRepository()
            val mockReturnValue = flow { emit(emptyList<ExchangeRateEntity>()) }

            every { exchangeRatesDao.findAll() } returns mockReturnValue

            // when
            val result = repository.findAll()

            // then
            result.collect { value ->
                value shouldBe emptyList()
            }
        }

        "list of exchange rates" {
            // given
            val repository = newRepository()
            val mockReturnValue =
                flow {
                    emit(
                        listOf<ExchangeRateEntity>(
                            ExchangeRateEntity("", "", 0.0),
                            ExchangeRateEntity("", "", 0.0),
                            ExchangeRateEntity("", "", 0.0),
                        ),
                    )
                }

            every { exchangeRatesDao.findAll() } returns mockReturnValue

            // when
            val result = repository.findAll()

            // then
            result.collect { value ->
                value shouldBe
                    listOf(
                        ExchangeRate(AssetCode(""), "", PositiveDouble(0.0), false),
                        ExchangeRate(AssetCode(""), "", PositiveDouble(0.0), false),
                        ExchangeRate(AssetCode(""), "", PositiveDouble(0.0), false),
                    )
            }
        }
    }
})
