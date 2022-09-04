package com.ivy.navigation.destinations.imports

import androidx.navigation.NamedNavArgument
import com.ivy.navigation.NavigationCommand

object Import {
    val root = object : NavigationCommand {
        override val route = "import"
        override val arguments = emptyList<NamedNavArgument>()
    }

    val importApp = ImportApp()
}