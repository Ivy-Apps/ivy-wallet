package com.ivy.home.event

sealed interface HomeEvent {
    object BalanceClick : HomeEvent
    object HiddenBalanceClick : HomeEvent

    data class BottomBarAction(val action: HomeBottomBarAction) : HomeEvent
}