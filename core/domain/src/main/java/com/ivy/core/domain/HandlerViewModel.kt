package com.ivy.core.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class HandlerViewModel<Event> : ViewModel() {
    private val events = MutableSharedFlow<Event>(replay = 0)

    protected abstract suspend fun handleEvent(event: Event)

    init {
        viewModelScope.launch {
            events.collect(::handleEvent)
        }
    }

    fun onEvent(event: Event) {
        viewModelScope.launch {
            events.emit(event)
        }
    }
}