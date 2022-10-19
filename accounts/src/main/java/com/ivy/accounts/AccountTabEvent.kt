package com.ivy.accounts

sealed interface AccountTabEvent {
    object NavigateToHome : AccountTabEvent

    data class BottomBarAction(val action: AccBottomBarAction) : AccountTabEvent
}