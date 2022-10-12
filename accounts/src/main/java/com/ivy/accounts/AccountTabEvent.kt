package com.ivy.accounts

sealed interface AccountTabEvent {
    data class BottomBarAction(val action: AccBottomBarAction) : AccountTabEvent
}