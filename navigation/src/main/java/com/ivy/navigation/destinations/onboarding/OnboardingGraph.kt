package com.ivy.navigation.destinations.onboarding

import com.ivy.navigation.NavGraph
import com.ivy.navigation.destinations.onboarding.screen.*

object OnboardingGraph : NavGraph {
    override val route = "onboarding"
    override val startDestination
        get() = loginOrOffline.route

    val loginOrOffline = LoginOffline
    val importBackup = Backup
    val setCurrency = SetCurrency
    val addAccounts = AddAccounts
    val addCategories = AddCategories
}