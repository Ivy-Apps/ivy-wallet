package com.ivy.wallet.ui.exchangerates

import com.ivy.wallet.ui.exchangerates.data.RateUi

data class RatesState(
    val baseCurrency: String,
    val manual: List<RateUi>,
    val automatic: List<RateUi>
)
