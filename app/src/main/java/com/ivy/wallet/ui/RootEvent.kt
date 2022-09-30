package com.ivy.wallet.ui

sealed interface RootEvent {
    object AppOpen : RootEvent
}