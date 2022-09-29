package com.ivy.home.event

sealed interface HomeEvent {
    object BalanceClick : HomeEvent
    object IncomeClick : HomeEvent
    object ExpenseClick : HomeEvent
    object HiddenBalanceClick : HomeEvent

    data class BottomBarAction(val action: HomeBottomBarAction) : HomeEvent
}