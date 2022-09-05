package com.ivy.navigation

import androidx.navigation.NavOptionsBuilder

internal sealed class NavigatorAction {
    data class Navigate(
        val route: String,
        val navOptions: NavOptionsBuilder.() -> Unit
    ) : NavigatorAction()

    object Back : NavigatorAction()
}