package com.ivy.navigation.destinations.main

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.navigation.NavDestination
import com.ivy.navigation.NavNode
import com.ivy.navigation.arg
import java.util.*

object TrnDetails : NavNode, NavDestination<UUID> {
    override val route = "trn/{trnId}"

    override val arguments = listOf(
        navArgument("trnId") {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun route(arg: UUID): String = "trn/$arg"

    override fun parse(entry: NavBackStackEntry): UUID =
        entry.arg("trnId") { UUID.fromString(it) }

}