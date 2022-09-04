package com.ivy.navigation.destinations.imports

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.NavigationCommand

class ImportApp : NavigationCommand {
    companion object {
        const val argFromApp = "fromApp"
    }

    override val route = "import/{$argFromApp}"
    override val arguments = listOf(
        navArgument(argFromApp) {
            type = NavType.StringType
            nullable = false
        }
    )
}