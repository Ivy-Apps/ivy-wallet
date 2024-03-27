package com.ivy.data.repository.mapper

import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test

class ExchangeRateMapperTest {

    private lateinit var mapper: ExchangeRateMapper

    @Before
    fun setup() {
        mapper = ExchangeRateMapper()
    }

    @Test
    fun `maps domain to entity`() {
        // given
        val mapper = ExchangeRateMapper()
        val exchangeRate =
            ExchangeRate(
                baseCurrency = AssetCode.unsafe("USD"),
                currency = AssetCode.unsafe("AAVE"),
                rate = PositiveDouble.unsafe(0.000943049049897979),
                manualOverride = false,
            )

        // when
        val result = with(mapper) { exchangeRate.toEntity() }

        // then
        result shouldBe
                ExchangeRateEntity(
                    baseCurrency = "USD",
                    currency = "AAVE",
                    rate = 0.000943049049897979,
                    manualOverride = false,
                )
    }

    @Test
    fun `maps entity to domain`() {
        // given
        val exchangeRateEntity =
            ExchangeRateEntity(
                baseCurrency = "USD",
                currency = "AAVE",
                rate = 0.000943049049897979,
                manualOverride = false,
            )

        // when
        val result = with(mapper) { exchangeRateEntity.toDomain() }

        // then
        result.getOrNull() shouldBe ExchangeRate(
            baseCurrency = AssetCode.unsafe("USD"),
            currency = AssetCode.unsafe("AAVE"),
            rate = PositiveDouble.unsafe(0.000943049049897979),
            manualOverride = false,
        )
    }
}
