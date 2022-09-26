package com.ivy.navigation.destinations.transaction

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen
import com.ivy.navigation.util.stringArg

object Transaction : Screen<String> {
    private const val ARG_TRN_ID = "trnId"

    override val route = "transaction/{$ARG_TRN_ID}"

    override val arguments = listOf(
        navArgument(ARG_TRN_ID) {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun destination(arg: String): DestinationRoute = "transaction/$arg"

    override fun parse(entry: NavBackStackEntry): String = entry.stringArg(ARG_TRN_ID)
}