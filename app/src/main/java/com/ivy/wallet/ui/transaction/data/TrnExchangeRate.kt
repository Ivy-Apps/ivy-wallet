package com.ivy.wallet.ui.transaction.data

import java.math.BigDecimal

data class TrnExchangeRate(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: BigDecimal
)