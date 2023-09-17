package com.ivy.core.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import com.ivy.core.datamodel.ExchangeRate
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
) {
    fun toDomain(): ExchangeRate = ExchangeRate(
        baseCurrency = baseCurrency,
        currency = currency,
        rate = rate
    )
}
