package com.ivy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

private val localNavigation = compositionLocalOf<Navigation> { error("No LocalNavigation") }

@Composable
fun NavigationRoot(
    navigation: Navigation,
    screenMapping: @Composable (screen: Screen?) -> Unit
) {
    CompositionLocalProvider(
        localNavigation provides navigation,
    ) {
        screenMapping(navigation.currentScreen)
    }
}

@Composable
fun navigation(): Navigation {
    return localNavigation.current
}