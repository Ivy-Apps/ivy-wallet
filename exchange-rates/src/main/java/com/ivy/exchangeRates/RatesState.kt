package com.ivy.exchangeRates

import androidx.compose.runtime.Immutable
import com.ivy.exchangeRates.data.RateUi

//Represents the state of exchange rates screen
@Immutable
data class RatesState(
    val baseCurrency: String,
    val manual: List<RateUi>,
    val automatic: List<RateUi>
)