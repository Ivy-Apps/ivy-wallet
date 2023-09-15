package com.ivy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalNavigation = compositionLocalOf<Navigation> { error("No LocalNavigation") }

@Composable
fun NavigationRoot(
    navigation: Navigation,
    screenMapping: @Composable (screen: Screen?) -> Unit
) {
    CompositionLocalProvider(
        LocalNavigation provides navigation,
    ) {
        screenMapping(navigation.currentScreen)
    }
}

@Composable
fun navigation(): Navigation {
    return LocalNavigation.current
}