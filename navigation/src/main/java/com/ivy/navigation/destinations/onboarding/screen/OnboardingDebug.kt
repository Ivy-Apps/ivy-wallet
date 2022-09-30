package com.ivy.navigation.destinations.onboarding.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen

object OnboardingDebug : Screen<Unit> {
    override val route = "onboarding/debug"
    override val arguments: List<NamedNavArgument> = emptyList()

    override fun parse(entry: NavBackStackEntry) {}

    override fun destination(arg: Unit): DestinationRoute = route
}