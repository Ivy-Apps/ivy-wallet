package com.ivy.navigation.destinations.main.trn

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.data.transaction.TrnType
import com.ivy.navigation.NavDestination
import com.ivy.navigation.NavNode
import com.ivy.navigation.arg
import com.ivy.navigation.optionalArg
import java.util.*

object NewTrn : NavNode, NavDestination<NewTrn.Arg> {
    data class Arg(
        val categoryId: UUID?,
        val accountId: UUID?,
        val trnType: TrnType,
    )

    private const val ARG_TRN_TYPE = "trnType"
    private const val ARG_CATEGORY_ID = "catId"
    private const val ARG_ACCOUNT_ID = "accId"

    override val route: String = "new/trn?trnType={$ARG_TRN_TYPE}" +
            "&catId={$ARG_CATEGORY_ID}&accId={$ARG_ACCOUNT_ID}"

    override val arguments = listOf(
        navArgument(ARG_TRN_TYPE) {
            type = NavType.StringType
            nullable = false
        },
        navArgument(ARG_CATEGORY_ID) {
            type = NavType.StringType
            nullable = true
        },
        navArgument(ARG_ACCOUNT_ID) {
            type = NavType.StringType
            nullable = true
        }
    )

    override fun parse(entry: NavBackStackEntry): Arg = Arg(
        trnType = entry.arg(ARG_TRN_TYPE) { TrnType.valueOf(it) },
        categoryId = entry.optionalArg(ARG_CATEGORY_ID) { UUID.fromString(it) },
        accountId = entry.optionalArg(ARG_ACCOUNT_ID) { UUID.fromString(it) },
    )

    override fun route(arg: Arg): String {
        val route = StringBuilder("new/trn?$ARG_TRN_TYPE=${arg.trnType.name}")
        if (arg.categoryId != null) {
            route.append("&$ARG_CATEGORY_ID=${arg.categoryId}")
        }
        if (arg.accountId != null) {
            route.append("&$ARG_ACCOUNT_ID=${arg.accountId}")
        }
        return route.toString()
    }
}