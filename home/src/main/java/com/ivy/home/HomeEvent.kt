package com.ivy.home

sealed class HomeEvent {
    object BalanceClick : HomeEvent()
    object HiddenBalanceClick : HomeEvent()
}