package com.ivy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.ivy.navigation.destinations.main.Main
import com.ivy.navigation.destinations.onboarding.Onboarding
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavigationRoot(
    navigator: Navigator,
) {
    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        navigator.actions.collectLatest { action ->
            when (action) {
                Navigator.Action.Back -> navController.popBackStack()
                is Navigator.Action.Navigate -> navController.navigate(
                    action.route,
                    action.navOptions
                )
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        navigation(
            startDestination = Onboarding.root.route,
            route = ""
        ) {
            composable(Onboarding.root.route) {

            }
            composable(Onboarding.importPrompt.route) {

            }
        }
        navigation(
            startDestination = "main",
            route = Main.main.route,
        ) {
            composable(Main.main.route) {

            }
            composable(Main.trnDetails.route) {

            }
        }
    }
}