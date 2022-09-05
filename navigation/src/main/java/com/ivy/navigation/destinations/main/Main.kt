package com.ivy.navigation.destinations.main

import androidx.navigation.NamedNavArgument
import com.ivy.navigation.NavNode

object Main {
    val main = object : NavNode {
        override val route = ""
        override val arguments = emptyList<NamedNavArgument>()
    }

    val trnDetails = TrnDetails
}