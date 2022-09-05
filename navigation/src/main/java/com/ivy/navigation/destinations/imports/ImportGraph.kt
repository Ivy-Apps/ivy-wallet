package com.ivy.navigation.destinations.imports

import com.ivy.navigation.NavGraph

object ImportGraph : NavGraph {
    override val route: String
        get() = "import"
    override val startDestination: String
        get() = TODO()

    val importApp = ImportApp
}