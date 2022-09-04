package com.ivy.navigation.destinations

import androidx.navigation.NamedNavArgument
import com.ivy.navigation.NavigationCommand
import com.ivy.navigation.destinations.onboarding.Onboarding

object Destination {
    val root = object : NavigationCommand {
        override val route = ""
        override val arguments = emptyList<NamedNavArgument>()
    }

    val onboarding = Onboarding.root


}