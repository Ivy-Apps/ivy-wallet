package com.ivy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ivy.navigation.destinations.main.Main
import com.ivy.navigation.graph.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavigationRoot(
    navigator: Navigator,
    onboardingScreens: OnboardingScreens,
    main: @Composable (Main.Tab?) -> Unit,
    transactionScreens: TransactionScreens,
    debugScreens: DebugScreens
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
        startDestination = Main.route
    ) {
        onboardingGraph(onboardingScreens)
        composable(Main.route) {
            main(Main.parse(it))
        }
        transactionScreens(transactionScreens)
        debug(debugScreens)
    }
}