package com.ivy.navigation.destinations.debug

import com.ivy.navigation.NavGraph
import com.ivy.navigation.destinations.debug.screen.Test

object DebugGraph : NavGraph {
    override val route: String = "debug"
    override val startDestination: String = Test.destination(Unit)

    val test = Test
}