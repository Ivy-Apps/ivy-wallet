package com.ivy.data.transaction

import com.ivy.data.CurrencyCode

data class TrnValue(
    val amount: Double,
    val currency: CurrencyCode
)