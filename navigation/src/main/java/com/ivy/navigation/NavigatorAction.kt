package com.ivy.navigation

import androidx.navigation.NavOptionsBuilder

internal sealed class NavigatorAction {
    data class Navigate(
        val command: NavigationCommand,
        val navOptions: NavOptionsBuilder.() -> Unit
    ) : NavigatorAction()

    object Back : NavigatorAction()
}