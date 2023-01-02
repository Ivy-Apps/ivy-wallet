package com.ivy.home

import com.ivy.main.base.MainBottomBarAction

sealed interface HomeEvent {
    object BalanceClick : HomeEvent
    object IncomeClick : HomeEvent
    object ExpenseClick : HomeEvent
    object HiddenBalanceClick : HomeEvent
    object MoreClick : HomeEvent

    data class BottomBarAction(val action: MainBottomBarAction) : HomeEvent
    object ShowBottomBar : HomeEvent
    object HideBottomBar : HomeEvent

    object AddTransfer : HomeEvent
    object AddIncome : HomeEvent
    object AddExpense : HomeEvent
}