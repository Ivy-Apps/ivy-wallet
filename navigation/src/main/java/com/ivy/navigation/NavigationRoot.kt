package com.ivy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ivy.navigation.destinations.debug.DebugGraph
import com.ivy.navigation.graph.DebugScreens
import com.ivy.navigation.graph.OnboardingScreens
import com.ivy.navigation.graph.debug
import com.ivy.navigation.graph.onboardingGraph
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavigationRoot(
    navigator: Navigator,
    onboardingScreens: OnboardingScreens,
    debugScreens: DebugScreens,
) {
    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        navigator.actions.collectLatest { action ->
            when (action) {
                Navigator.Action.Back -> navController.popBackStack()
                is Navigator.Action.Navigate -> navController.navigate(
                    route = action.destination,
                    builder = action.navOptions
                )
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = DebugGraph.route
    ) {
        onboardingGraph(onboardingScreens)
        debug(debugScreens)
    }
}