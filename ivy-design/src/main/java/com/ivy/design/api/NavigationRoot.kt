package com.ivy.design.api

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.ivy.design.navigation.Navigation
import com.ivy.design.navigation.Screen

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