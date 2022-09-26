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

    protected abstract fun initialUiState(): UiState

    protected abstract fun stateFlow(): Flow<State>

    protected abstract suspend fun mapToUiState(state: State): UiState

    protected abstract suspend fun handleEvent(event: Event)

    private var stateFlow: StateFlow<State>? = null
    private var uiStateFlow: StateFlow<UiState>? = null

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
        get() = uiStateFlow ?: run {
            uiStateFlow = state.map {
                mapToUiState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
                initialValue = initialUiState(),
            )
            uiStateFlow!!
        }

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