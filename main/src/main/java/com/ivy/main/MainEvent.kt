package com.ivy.main

import com.ivy.navigation.destinations.main.Main

sealed interface MainEvent {
    data class SelectTab(val tab: Main.Tab?) : MainEvent

    object SwitchSelectedTab : MainEvent
}