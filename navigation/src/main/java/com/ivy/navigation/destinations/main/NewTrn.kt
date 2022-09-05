package com.ivy.navigation.destinations.main

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
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

    override val route: String = "new/trn?catId={catId}&accId={accId}&trnType={trnType}"
    override val arguments: List<NamedNavArgument>
        get() = TODO("Not yet implemented")


    override fun parse(entry: NavBackStackEntry): Arg = Arg(
        categoryId = entry.optionalArg("catId") { UUID.fromString(it) },
        accountId = entry.optionalArg("accId") { UUID.fromString(it) },
        trnType = entry.arg("trnType") { TrnType.valueOf(it) },
    )

    override fun route(arg: Arg): String {
        TODO("Not yet implemented")
    }
}