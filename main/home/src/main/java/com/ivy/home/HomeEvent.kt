package com.ivy.home

sealed interface HomeEvent {
    object BalanceClick : HomeEvent
    object IncomeClick : HomeEvent
    object ExpenseClick : HomeEvent
    object HiddenBalanceClick : HomeEvent
    object MoreClick : HomeEvent

    sealed interface BottomBar : HomeEvent {
        object Show : BottomBar
        object Hide : BottomBar
        object AddClick : BottomBar
        object AccountsClick : BottomBar
    }

    object AddTransfer : HomeEvent
    object AddIncome : HomeEvent
    object AddExpense : HomeEvent
}