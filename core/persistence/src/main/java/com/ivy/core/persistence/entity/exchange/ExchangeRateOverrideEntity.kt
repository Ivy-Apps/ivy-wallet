package com.ivy.core.persistence.entity.exchange

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.ivy.data.SyncState
import java.time.Instant

@Entity(
    tableName = "exchange_rates_override",
    primaryKeys = ["baseCurrency", "currency"]
)
data class ExchangeRateOverrideEntity(
    @ColumnInfo(name = "baseCurrency", index = true)
    val baseCurrency: String,
    @ColumnInfo(name = "currency", index = true)
    val currency: String,
    @ColumnInfo(name = "rate")
    val rate: Double,

    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Instant,
)