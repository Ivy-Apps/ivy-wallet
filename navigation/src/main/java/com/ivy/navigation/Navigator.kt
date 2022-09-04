package com.ivy.navigation

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Stable
@Singleton
class Navigator @Inject constructor() {
    private val _commands = MutableSharedFlow<NavigationCommand>(
        replay = 0,
        extraBufferCapacity = 10
    )
    val commands: Flow<NavigationCommand> = _commands

    fun navigate(command: NavigationCommand) {
        _commands.tryEmit(command)
    }
}