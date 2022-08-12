package com.ivy.exchange

import androidx.room.Entity

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