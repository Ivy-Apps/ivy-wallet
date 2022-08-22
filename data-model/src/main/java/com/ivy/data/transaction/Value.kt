package com.ivy.data.transaction

import com.ivy.data.CurrencyCode

data class Value(
    val amount: Double,
    val currency: CurrencyCode
)