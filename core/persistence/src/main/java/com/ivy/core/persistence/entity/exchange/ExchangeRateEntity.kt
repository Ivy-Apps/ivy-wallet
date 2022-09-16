package com.ivy.core.persistence.entity.exchange

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.ivy.data.exchange.ExchangeProvider

@Entity(
    tableName = "exchange_rates",
    primaryKeys = ["baseCurrency", "currency"]
)
data class ExchangeRateEntity(
    @ColumnInfo(name = "baseCurrency", index = true)
    val baseCurrency: String,
    @ColumnInfo(name = "currency", index = true)
    val currency: String,
    @ColumnInfo(name = "rate")
    val rate: Double,
    @ColumnInfo(name = "provider", index = true)
    val provider: ExchangeProvider?,
)