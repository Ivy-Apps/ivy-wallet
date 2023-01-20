package com.ivy.core.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Toaster @Inject constructor() {
    private val _messagesFlow = MutableSharedFlow<String>()
    val messages: Flow<String> = _messagesFlow

    suspend fun show(message: String) {
        _messagesFlow.emit(message)
    }
}