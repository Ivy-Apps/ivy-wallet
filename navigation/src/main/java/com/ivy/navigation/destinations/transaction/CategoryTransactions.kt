package com.ivy.navigation.destinations.transaction

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen
import com.ivy.navigation.util.stringArg

object CategoryTransactions : Screen<String> {
    private const val ARG_CAT_ID = "catId"

    override val route = "category-transactions/{$ARG_CAT_ID}"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(ARG_CAT_ID) {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun destination(arg: String): DestinationRoute = "category-transactions/$arg"

    override fun parse(entry: NavBackStackEntry): String = entry.stringArg(ARG_CAT_ID)
}