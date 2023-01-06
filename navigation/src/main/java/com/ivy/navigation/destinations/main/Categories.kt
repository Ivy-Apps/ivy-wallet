package com.ivy.navigation.destinations.main

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen

object Categories : Screen<Unit> {
    override val route: String = "categories"
    override val arguments: List<NamedNavArgument> = emptyList()

    override fun parse(entry: NavBackStackEntry): Unit = Unit

    override fun destination(arg: Unit): DestinationRoute = route
}