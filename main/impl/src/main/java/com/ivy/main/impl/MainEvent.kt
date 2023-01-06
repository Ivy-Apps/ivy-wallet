package com.ivy.main.impl

import com.ivy.navigation.destinations.main.Main

sealed interface MainEvent {
    data class SelectTab(val tab: Main.Tab?) : MainEvent

    object SwitchSelectedTab : MainEvent
}