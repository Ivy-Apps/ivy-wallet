package com.ivy.accounts

import com.ivy.main.base.MainBottomBarAction

sealed interface AccountTabEvent {
    object NavigateToHome : AccountTabEvent

    data class BottomBarAction(val action: MainBottomBarAction) : AccountTabEvent
    object ShowBottomBar : AccountTabEvent
    object HideBottomBar : AccountTabEvent
}