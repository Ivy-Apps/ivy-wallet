package com.ivy.debug

import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.data.CurrencyCode

data class TestStateUi(
    val selectedPeriodUi: SelectedPeriodUi,
    val baseCurrency: CurrencyCode,
)