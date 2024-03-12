package com.ivy.data.repository.mapper

import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExchangeRateMapperTest : FreeSpec({

    "maps ExchangeRate to ExchangeRateEntity" {
        // given
        val mapper = ExchangeRateMapper()
        val exchangeRate =
            ExchangeRate(
                baseCurrency = "USD",
                currency = "AAVE",
                rate = 0.000943049049897979,
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

    "maps ExchangeRateEntity to ExchangeRate" {
        // given
        val mapper = ExchangeRateMapper()
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
        result shouldBe
            ExchangeRate(
                baseCurrency = "USD",
                currency = "AAVE",
                rate = 0.000943049049897979,
                manualOverride = false,
            )
    }
})
