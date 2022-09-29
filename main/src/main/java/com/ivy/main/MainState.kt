package com.ivy.main

import androidx.compose.runtime.Immutable
import com.ivy.navigation.destinations.main.Main

@Immutable
data class MainState(
    val selectedTab: Main.Tab
)