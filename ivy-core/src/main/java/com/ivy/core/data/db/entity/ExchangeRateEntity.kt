package com.ivy.core.data.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.ivy.core.data.model.ExchangeRate

@Keep
@Entity(tableName = "exchange_rates", primaryKeys = ["baseCurrency", "currency"])
data class ExchangeRateEntity(
    @SerializedName("baseCurrency")
    val baseCurrency: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("rate")
    val rate: Double,
    @SerializedName("manualOverride")
    val manualOverride: Boolean = false,
) {
    fun toDomain(): ExchangeRate = ExchangeRate(
        baseCurrency = baseCurrency,
        currency = currency,
        rate = rate
    )
}
