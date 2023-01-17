package com.ivy.core.domain.action

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * ## ATTENTION: For this to work, annotate the class that extends it with "@Singleton".
 * Don't forget to annotate your class with [javax.inject.Singleton].
 */
abstract class SignalFlow<T> {
    private val sharedFlow = MutableSharedFlow<T>(replay = 1)
    private var initialSignalSent = false

    var enabled = true
        private set

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    abstract fun initialSignal(): T

    suspend fun send(signal: T) {
        if (enabled) {
            sharedFlow.emit(signal)
        }
    }

    fun receive(): Flow<T> {
        if (!initialSignalSent) {
            sharedFlow.tryEmit(initialSignal())
            initialSignalSent = true
        }
        return sharedFlow
    }
}