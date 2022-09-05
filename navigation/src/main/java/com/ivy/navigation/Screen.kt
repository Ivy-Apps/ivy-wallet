package com.ivy.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry

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