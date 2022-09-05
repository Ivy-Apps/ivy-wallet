package com.ivy.navigation.destinations.imports

import androidx.navigation.NamedNavArgument
import com.ivy.navigation.NavNode

object Import {
    val root = object : NavNode {
        override val route = "import"
        override val arguments = emptyList<NamedNavArgument>()
    }

    val importApp = ImportApp
}