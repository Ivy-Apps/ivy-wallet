package com.ivy.navigation.destinations.transaction

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen
import com.ivy.navigation.util.stringArg

object AccountTransactions : Screen<String> {
    private const val ARG_ACC_ID = "accId"

    override val route = "account-transactions/{$ARG_ACC_ID}"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(ARG_ACC_ID) {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun destination(arg: String): DestinationRoute = "account-transactions/$arg"

    override fun parse(entry: NavBackStackEntry): String = entry.stringArg(ARG_ACC_ID)
}