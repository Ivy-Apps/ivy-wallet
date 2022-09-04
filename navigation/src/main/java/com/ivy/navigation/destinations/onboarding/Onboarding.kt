package com.ivy.navigation.destinations.onboarding

import androidx.navigation.NamedNavArgument
import com.ivy.navigation.NavigationCommand

object Onboarding {
    val root = object : NavigationCommand {
        override val route = "onboarding"
        override val arguments = emptyList<NamedNavArgument>()
    }

    val importPrompt = object : NavigationCommand {
        override val route = "onboarding/import-prompt"
        override val arguments = emptyList<NamedNavArgument>()
    }
}