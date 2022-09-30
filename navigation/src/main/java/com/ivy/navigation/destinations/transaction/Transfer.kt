package com.ivy.navigation.destinations.transaction

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen
import com.ivy.navigation.util.stringArg

object Transfer : Screen<String> {
    private const val ARG_BATCH_ID = "batchId"

    override val route = "transfer/{$ARG_BATCH_ID}"

    override val arguments = listOf(
        navArgument(ARG_BATCH_ID) {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun destination(arg: String): DestinationRoute = "transfer/$arg"

    override fun parse(entry: NavBackStackEntry): String = entry.stringArg(ARG_BATCH_ID)
}