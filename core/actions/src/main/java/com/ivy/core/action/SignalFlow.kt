package com.ivy.core.action

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

// TODO: add KDoc
abstract class SignalFlow<T> {
    private val sharedFlow = MutableSharedFlow<T>(replay = 1)
    private var initialSignalSent = false

    abstract fun initialSignal(): T

    suspend fun send(signal: T) {
        sharedFlow.emit(signal)
    }

    suspend fun receive(): Flow<T> {
        if (!initialSignalSent) {
            withContext(Dispatchers.Default) {
                sharedFlow.emit(initialSignal())
            }
            initialSignalSent = true
        }
        return sharedFlow
    }
}