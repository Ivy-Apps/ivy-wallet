package com.ivy.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry

interface NavNode {
    val route: String
    val arguments: List<NamedNavArgument>
}

interface NavDestination<Arg> {
    fun route(arg: Arg): String
    fun parse(entry: NavBackStackEntry): Arg
}

fun <T> NavBackStackEntry.arg(key: String, transform: (String) -> T): T =
    arguments?.getString(key)?.let(transform) ?: error("missing '$key' argument")

fun <T> NavBackStackEntry.optionalArg(key: String, transform: (String) -> T): T? =
    arguments?.getString(key)?.let(transform)