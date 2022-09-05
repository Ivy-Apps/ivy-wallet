package com.ivy.navigation

import androidx.navigation.NavBackStackEntry

interface NavDestination<Arg> {
    fun route(arg: Arg): String
    fun parse(entry: NavBackStackEntry): Arg
}