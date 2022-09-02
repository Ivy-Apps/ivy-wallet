package com.ivy.core.action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class FlowViewModel<State, UiState, Event> : ViewModel() {
    private val events = MutableSharedFlow<Event>(replay = 0)

    init {
        viewModelScope.launch {
            state = stateFlow()
                .onStart { TestIdlingResource.increment() }
                .onCompletion { TestIdlingResource.decrement() }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
                    initialValue = initialState(),
                )
        }
        viewModelScope.launch {
            events.collect(::handleEvent)
        }
    }

    abstract fun initialState(): State

    abstract suspend fun stateFlow(): Flow<State>

    abstract fun mapToUiState(state: StateFlow<State>): StateFlow<UiState>

    abstract suspend fun handleEvent(event: Event)

    protected lateinit var state: StateFlow<State>
        private set

    val uiState: StateFlow<UiState> by lazy { mapToUiState(state) }

    fun onEvent(event: Event) {
        viewModelScope.launch {
            events.emit(event)
        }
    }
}