package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import com.ivy.wallet.domain.data.core.ExchangeRate

@Entity(tableName = "exchange_rates", primaryKeys = ["baseCurrency", "currency"])
data class ExchangeRateEntity(
    val baseCurrency: String,
    val currency: String,
    val rate: Double,
) {
    fun toDomain(): ExchangeRate = ExchangeRate(
        baseCurrency = baseCurrency,
        currency = currency,
        rate = rate
    )
}