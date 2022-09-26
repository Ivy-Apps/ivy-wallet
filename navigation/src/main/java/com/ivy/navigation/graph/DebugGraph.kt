package com.ivy.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ivy.navigation.destinations.debug.DebugGraph
import com.ivy.navigation.destinations.debug.screen.Test

@Immutable
data class DebugScreens(
    val test: @Composable () -> Unit
)

internal fun NavGraphBuilder.debug(screens: DebugScreens) {
    navigation(
        route = DebugGraph.route,
        startDestination = DebugGraph.startDestination
    ) {
        composable(Test.route) {
            screens.test()
        }
    }
}