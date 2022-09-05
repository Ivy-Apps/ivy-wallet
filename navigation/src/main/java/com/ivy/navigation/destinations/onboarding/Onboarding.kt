package com.ivy.navigation.destinations.onboarding

import androidx.navigation.NamedNavArgument
import com.ivy.navigation.NavNode

object Onboarding {

    val root = object : NavNode {
        override val route = ""
        override val arguments = emptyList<NamedNavArgument>()
    }

    val importPrompt = object : NavNode {
        override val route = "import-prompt"
        override val arguments = emptyList<NamedNavArgument>()
    }
}