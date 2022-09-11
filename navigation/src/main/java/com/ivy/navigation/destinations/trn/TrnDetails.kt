package com.ivy.navigation.destinations.trn

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen
import com.ivy.navigation.util.arg
import java.util.*

object TrnDetails : Screen<UUID> {
    private const val ARG_TRN_ID = "trnId"

    override val route = "trn/{$ARG_TRN_ID}"

    override val arguments = listOf(
        navArgument("trnId") {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun destination(arg: UUID): DestinationRoute = "trn/$arg"

    override fun parse(entry: NavBackStackEntry): UUID =
        entry.arg(ARG_TRN_ID) { UUID.fromString(it) }

}