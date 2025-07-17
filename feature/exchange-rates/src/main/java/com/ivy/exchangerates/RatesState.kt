package com.ivy.exchangerates

import com.ivy.exchangerates.data.RateUi
import kotlinx.collections.immutable.ImmutableList

data class RatesState(
    val baseCurrency: String,
    val manual: ImmutableList<RateUi>,
    val automatic: ImmutableList<RateUi>
)
