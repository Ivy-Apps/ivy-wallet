package com.ivy.navigation.destinations.imports

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.NavNode

object ImportApp : NavNode {
    fun parse(entry: NavBackStackEntry): String =
        entry.arguments?.getString("importApp") ?: error("argument missing")

    override val route = "import/{importApp}"

    override val arguments = listOf(
        navArgument("importApp") {
            type = NavType.StringType
            nullable = false
        }
    )
}