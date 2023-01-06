package com.ivy.navigation.destinations.settings

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen

object Settings : Screen<Unit> {
    override val route = "/settings"
    override val arguments: List<NamedNavArgument> = emptyList()

    override fun parse(entry: NavBackStackEntry) {}

    override fun destination(arg: Unit): DestinationRoute = route
}