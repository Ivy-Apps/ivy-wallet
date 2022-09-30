package com.ivy.navigation.destinations.debug.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen

object Test : Screen<Unit> {
    override val route: String = "debug/test"
    override val arguments: List<NamedNavArgument> = emptyList()

    override fun destination(arg: Unit): DestinationRoute = route

    override fun parse(entry: NavBackStackEntry) {}
}