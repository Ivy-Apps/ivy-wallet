package com.ivy.navigation.destinations

import androidx.navigation.NamedNavArgument
import com.ivy.navigation.NavNode
import com.ivy.navigation.destinations.onboarding.Onboarding

object Destination {
    val root = object : NavNode {
        override val route = ""
        override val arguments = emptyList<NamedNavArgument>()
    }

    val onboarding = Onboarding


}