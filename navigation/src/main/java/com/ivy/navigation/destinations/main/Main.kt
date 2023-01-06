package com.ivy.navigation.destinations.main

import androidx.compose.runtime.Immutable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen
import com.ivy.navigation.util.optionalArg
import com.ivy.navigation.util.string

object Main : Screen<Main.Tab?> {
    @Immutable
    enum class Tab {
        Home, Accounts
    }

    private const val ARG_TAB = "tab"

    override val route: String = "main?tab={$ARG_TAB}"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(ARG_TAB) {
            type = NavType.StringType
            nullable = true
        }
    )

    override fun parse(entry: NavBackStackEntry): Tab? =
        entry.optionalArg(ARG_TAB, string()) { Tab.valueOf(it) }

    override fun destination(arg: Tab?): DestinationRoute =
        if (arg != null) "main?tab=${arg.name}" else "main"
}