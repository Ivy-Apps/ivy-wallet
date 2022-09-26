package com.ivy.navigation.destinations.transaction

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen

object NewTransfer : Screen<Unit> {
    override val route: String = "new/transfer"

    override val arguments: List<NamedNavArgument> = emptyList()

    override fun destination(arg: Unit): DestinationRoute = route

    override fun parse(entry: NavBackStackEntry) {}
}