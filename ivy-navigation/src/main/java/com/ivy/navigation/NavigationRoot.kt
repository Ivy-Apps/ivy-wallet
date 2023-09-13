package com.ivy.navigation

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalNavigation = compositionLocalOf<Navigation> { error("No LocalNavigation") }

@Composable
fun BoxWithConstraintsScope.NavigationRoot(
    navigation: Navigation,
    ScreenMapping: @Composable (screen: Screen?) -> Unit
) {
    CompositionLocalProvider(
        LocalNavigation provides navigation,
    ) {
        ScreenMapping(navigation.currentScreen)
    }
}

@Composable
fun navigation(): Navigation {
    return LocalNavigation.current
}