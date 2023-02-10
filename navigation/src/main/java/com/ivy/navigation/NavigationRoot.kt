package com.ivy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ivy.navigation.destinations.Destination
import com.ivy.navigation.destinations.imports.ImportBackup
import com.ivy.navigation.destinations.main.Accounts
import com.ivy.navigation.destinations.main.Categories
import com.ivy.navigation.destinations.main.Home
import com.ivy.navigation.destinations.main.MoreMenu
import com.ivy.navigation.destinations.other.AddFrame
import com.ivy.navigation.destinations.other.ExchangeRate
import com.ivy.navigation.destinations.settings.Settings
import com.ivy.navigation.graph.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavigationRoot(
    navigator: Navigator,
    onboardingScreens: OnboardingScreens,
    home: @Composable () -> Unit,
    accounts: @Composable () -> Unit,
    moreMenu: @Composable () -> Unit,
    categories: @Composable () -> Unit,
    settings: @Composable () -> Unit,
    transactionScreens: TransactionScreens,
    addFrame: @Composable () -> Unit,
    importBackup: @Composable () -> Unit,
    exchangeRates: @Composable () -> Unit,
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
        startDestination = Destination.home.route
    ) {
        onboardingGraph(onboardingScreens)
        composable(Home.route) {
            home()
        }
        composable(Accounts.route) {
            accounts()
        }
        composable(MoreMenu.route) {
            moreMenu()
        }
        composable(Categories.route) {
            categories()
        }
        composable(Settings.route) {
            settings()
        }
        transactionScreens(transactionScreens)
        debug(debugScreens)
        composable(AddFrame.route) {
            addFrame()
        }
        composable(ImportBackup.route) {
            importBackup()
        }
        composable(ExchangeRate.route) {
            exchangeRates()
        }
    }
}