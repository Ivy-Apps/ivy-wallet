package com.ivy.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ivy.navigation.destinations.Destination
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavigationRoot(
    navigator: Navigator,
    navGraph: NavGraphBuilder.() -> Unit
) {
    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        navigator.actions.collectLatest { action ->
            when (action) {
                NavigatorAction.Back -> navController.popBackStack()
                is NavigatorAction.Navigate -> navController.navigate(
                    action.command.route,
                    action.navOptions
                )
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = Destination.root.route
    ) {
        composable(Destination.root.route) {
            // start destination
            val activity = LocalContext.current as Activity
            BackHandler(enabled = true) {
                activity.finish()
            }
        }
        navGraph()
    }
}