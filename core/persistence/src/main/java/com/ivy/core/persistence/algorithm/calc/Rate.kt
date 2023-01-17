package com.ivy.core.persistence.algorithm.calc

import androidx.room.ColumnInfo
import com.ivy.data.CurrencyCode

data class Rate(
    @ColumnInfo(name = "rate")
    val rate: Double,
    @ColumnInfo(name = "currency")
    val currency: CurrencyCode
)