package com.ivy.navigation

sealed class NavigatorCommand {
    data class Navigate(val command: NavigationCommand) : NavigatorCommand()
    // TODO: Add pop backstack
}