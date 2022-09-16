package com.ivy.temp.persistence

import androidx.room.Entity

@Deprecated("old")
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