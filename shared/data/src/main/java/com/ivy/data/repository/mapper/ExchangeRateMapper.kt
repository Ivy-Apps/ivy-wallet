package com.ivy.data.repository.mapper

import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import javax.inject.Inject

class ExchangeRateMapper @Inject constructor() {

    fun ExchangeRateEntity.toDomain(): ExchangeRate {
        return ExchangeRate(
            baseCurrency = baseCurrency,
            currency = currency,
            rate = rate,
            manualOverride = manualOverride
        )
    }

    fun ExchangeRate.toEntity(): ExchangeRateEntity {
        return ExchangeRateEntity(
            baseCurrency = baseCurrency,
            currency = currency,
            rate = rate,
            manualOverride = manualOverride
        )
    }

}