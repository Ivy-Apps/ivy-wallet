package com.ivy.core.action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class FlowViewModel<S, E> : ViewModel() {
    private val events = MutableSharedFlow<E>(replay = 0)

    init {
        viewModelScope.launch {
            events.collect(::handleEvent)
        }
    }

    abstract fun initialState(): S

    abstract fun stateFlow(): Flow<S>

    abstract suspend fun handleEvent(event: E)

    val state: StateFlow<S> by lazy {
        stateFlow()
            .onStart { TestIdlingResource.increment() }
            .onCompletion { TestIdlingResource.decrement() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = initialState(),
            )
    }

    fun onEvent(event: E) {
        viewModelScope.launch {
            events.emit(event)
        }
    }
}