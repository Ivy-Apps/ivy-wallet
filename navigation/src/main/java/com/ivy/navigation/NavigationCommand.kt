package com.ivy.navigation

import androidx.navigation.NamedNavArgument

interface NavigationCommand {
    val route: String
    val arguments: List<NamedNavArgument>
}