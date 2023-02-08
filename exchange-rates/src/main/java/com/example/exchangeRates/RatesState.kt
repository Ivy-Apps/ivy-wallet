package com.example.exchangeRates

import com.example.exchangeRates.data.RateUi

//Represents the state of exchange rates screen
data class RatesState(
    val baseCurrency: String,
    val manual: List<RateUi>,
    val automatic: List<RateUi>
)