package com.ivy.wallet.ui.exchangerates

import com.ivy.wallet.ui.exchangerates.data.RateUi

sealed interface RatesEvent {
    data class Search(val query: String) : RatesEvent
    data class RemoveOverride(val rate: RateUi) : RatesEvent
    data class UpdateRate(val rate: RateUi, val newRate: Double) : RatesEvent
    data class AddRate(val rate: RateUi) : RatesEvent
}
