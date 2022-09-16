package com.ivy.core.persistence.entity.exchange.converter

import androidx.room.TypeConverter
import com.ivy.data.exchange.ExchangeProvider

class ExchangeRateTypeConverter {
    // region ExchangeProvider
    @TypeConverter
    fun ser(provider: ExchangeProvider?): Int? = provider?.code

    @TypeConverter
    fun exchangeProvider(code: Int?): ExchangeProvider? =
        code?.let(ExchangeProvider::fromCode)
    // endregion
}