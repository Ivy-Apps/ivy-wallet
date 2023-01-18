package com.ivy.accounts

sealed interface AccountsEvent {
    object NavigateToHome : AccountsEvent

    object BottomBarActionClick : AccountsEvent
    object ShowBottomBar : AccountsEvent
    object HideBottomBar : AccountsEvent
}