package com.example.exchangeRates

import com.example.exchangeRates.data.RateUi

sealed interface RatesEvent{
    data class Search(val query: String) : RatesEvent
    data class RemoveOverride(val rate: RateUi) : RatesEvent
    data class UpdateRate(val rate: RateUi, val newRate: Double) : RatesEvent
    data class AddRate(val rate: RateUi) : RatesEvent
}