package com.ivy.data.repository.mapper

import arrow.core.Either
import arrow.core.raise.either
import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import javax.inject.Inject

class ExchangeRateMapper @Inject constructor() {
    fun ExchangeRateEntity.toDomain(): Either<String, ExchangeRate> = either {
        ExchangeRate(
            baseCurrency = AssetCode.from(baseCurrency).bind(),
            currency = AssetCode.from(currency).bind(),
            rate = PositiveDouble.from(rate).bind(),
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
