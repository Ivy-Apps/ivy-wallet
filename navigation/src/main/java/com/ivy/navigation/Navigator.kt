package com.ivy.navigation

import androidx.compose.runtime.Stable
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Stable
@Singleton
class Navigator @Inject constructor() {
    private val _actions = MutableSharedFlow<NavigatorAction>(
        replay = 0,
        extraBufferCapacity = 10
    )
    internal val actions: Flow<NavigatorAction> = _actions

    fun navigate(route: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
        _actions.tryEmit(
            NavigatorAction.Navigate(route = route, navOptions = navOptions)
        )
    }

    fun back() {
        _actions.tryEmit(NavigatorAction.Back)
    }
}