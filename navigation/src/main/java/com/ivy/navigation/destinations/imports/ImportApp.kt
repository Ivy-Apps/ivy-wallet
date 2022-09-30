package com.ivy.navigation.destinations.imports

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.Screen
import com.ivy.navigation.util.stringArg

object ImportApp : Screen<String> {
    private const val ARG_IMPORT_APP = "importApp"

    override val route: String = "import/{$ARG_IMPORT_APP}"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(ARG_IMPORT_APP) {
            nullable = false
            type = NavType.StringType
        }
    )

    override fun destination(arg: String): String = "import/$arg"
    override fun parse(entry: NavBackStackEntry): String = entry.stringArg(ARG_IMPORT_APP)
}