package com.ivy.balance

import com.ivy.legacy.data.model.TimePeriod

sealed interface BalanceEvent {
    data class OnSetPeriod(val timePeriod: TimePeriod) : BalanceEvent
    object OnPreviousMonth : BalanceEvent
    object OnNextMonth : BalanceEvent
}