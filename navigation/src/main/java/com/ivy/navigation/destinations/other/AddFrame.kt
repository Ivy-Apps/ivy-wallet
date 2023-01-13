package com.ivy.navigation.destinations.other

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen

object AddFrame : Screen<Unit> {
    override val route = "add-frame"
    override val arguments: List<NamedNavArgument> = emptyList()

    override fun parse(entry: NavBackStackEntry) {}

    override fun destination(arg: Unit): DestinationRoute = route
}