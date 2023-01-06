package com.ivy.main.impl

import androidx.compose.runtime.Immutable
import com.ivy.navigation.destinations.main.Main

@Immutable
data class MainState(
    val selectedTab: Main.Tab,
    val bottomBarVisible: Boolean,
)