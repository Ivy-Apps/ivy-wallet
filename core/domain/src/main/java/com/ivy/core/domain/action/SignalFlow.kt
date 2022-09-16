package com.ivy.core.domain.action

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

// TODO: add KDoc
abstract class SignalFlow<T> {
    private val sharedFlow = MutableSharedFlow<T>(replay = 1)
    private var initialSignalSent = false

    abstract fun initialSignal(): T

    suspend fun send(signal: T) {
        sharedFlow.emit(signal)
    }

    fun receive(): Flow<T> {
        if (!initialSignalSent) {
            sharedFlow.tryEmit(initialSignal())
            initialSignalSent = true
        }
        return sharedFlow
    }
}