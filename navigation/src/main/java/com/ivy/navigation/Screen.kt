package com.ivy.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry

abstract class NoArgsScreen : NavNode, NavDestination<Unit> {
    override val arguments: List<NamedNavArgument> = emptyList()
    override fun parse(entry: NavBackStackEntry) {}
    override fun destination(arg: Unit): DestinationRoute = route
}

interface Screen<Arg> : NavNode, NavDestination<Arg>

interface NavNode {
    val route: String
    val arguments: List<NamedNavArgument>
}

typealias DestinationRoute = String

interface NavDestination<Arg> {
    fun destination(arg: Arg): DestinationRoute
    fun parse(entry: NavBackStackEntry): Arg
}