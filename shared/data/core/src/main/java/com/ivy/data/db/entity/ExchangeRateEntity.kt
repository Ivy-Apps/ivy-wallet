package com.ivy.data.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Entity(tableName = "exchange_rates", primaryKeys = ["baseCurrency", "currency"])
data class ExchangeRateEntity(
    @SerialName("baseCurrency")
    val baseCurrency: String,
    @SerialName("currency")
    val currency: String,
    @SerialName("rate")
    val rate: Double,
    @SerialName("manualOverride")
    val manualOverride: Boolean = false,
)
