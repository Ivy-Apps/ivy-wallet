package com.ivy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest


@Composable
fun NavigationRoot(
    navigator: Navigator,
    navGraph: NavGraphBuilder.() -> Unit
) {
    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        navigator.commands.collectLatest { command ->
            navController.navigate(command.destination)
        }
    }
    navController.popBackStack()
    NavHost(
        navController = navController,
        startDestination = "none"
    ) {
        composable("none") {
            // start destination
        }
        navGraph()
    }
}