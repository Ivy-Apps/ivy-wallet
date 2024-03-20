package com.ivy.data.repository.mapper

import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import javax.inject.Inject

class ExchangeRateMapper @Inject constructor() {
    fun ExchangeRateEntity.toDomain(): ExchangeRate {
        return ExchangeRate(
            baseCurrency = AssetCode(baseCurrency),
            currency = AssetCode(currency),
            rate = PositiveDouble(rate),
            manualOverride = manualOverride,
            )
    }

    fun ExchangeRate.toEntity(): ExchangeRateEntity {
        return ExchangeRateEntity(
            baseCurrency = baseCurrency.code,
            currency = currency.code,
            rate = rate.value,
            manualOverride = manualOverride,
            )
    }
}
