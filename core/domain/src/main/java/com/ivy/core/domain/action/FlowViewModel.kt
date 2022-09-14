package com.ivy.core.domain.action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class FlowViewModel<State, UiState, Event> : ViewModel() {
    private val events = MutableSharedFlow<Event>(replay = 0)

    protected abstract fun initialState(): State

    protected abstract fun stateFlow(): Flow<State>

    protected abstract fun mapToUiState(state: StateFlow<State>): StateFlow<UiState>

    protected abstract suspend fun handleEvent(event: Event)

    private var stateFlow: StateFlow<State>? = null

    protected val state: StateFlow<State>
        get() = stateFlow ?: run {
            stateFlow = stateFlow()
                .onStart { TestIdlingResource.increment() }
                .onCompletion { TestIdlingResource.decrement() }
                .flowOn(Dispatchers.Default)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
                    initialValue = initialState(),
                )
            stateFlow!!
        }

    val uiState: StateFlow<UiState>
        get() = mapToUiState(state)

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