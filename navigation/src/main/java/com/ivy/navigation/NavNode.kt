package com.ivy.navigation

import androidx.navigation.NamedNavArgument

interface NavNode {
    val route: String
    val arguments: List<NamedNavArgument>
}