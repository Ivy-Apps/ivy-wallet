package com.ivy.debug

import com.ivy.data.CurrencyCode

sealed interface TestEvent {
    data class BaseCurrencyChange(val currency: CurrencyCode) : TestEvent
}