package com.ivy.wallet.ui

import android.content.Intent

sealed interface RootEvent {
    object AppOpen : RootEvent
    data class ShortcutClick(val intent: Intent) : RootEvent

}