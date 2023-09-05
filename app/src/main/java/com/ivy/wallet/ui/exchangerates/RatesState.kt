package com.ivy.wallet.ui.exchangerates

import com.google.firebase.crashlytics.internal.model.ImmutableList
import com.ivy.wallet.ui.exchangerates.data.RateUi

data class RatesState(
    val baseCurrency: String,
    val manual: ImmutableList<RateUi>,
    val automatic: List<RateUi>
)
