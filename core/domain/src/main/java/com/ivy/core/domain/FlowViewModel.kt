package com.ivy.core.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class FlowViewModel<State, UiState, Event> : ViewModel() {
    private val events = MutableSharedFlow<Event>(replay = 0)

    protected abstract fun initialState(): State

    protected abstract fun initialUiState(): UiState

    protected abstract fun stateFlow(): Flow<State>

    protected abstract suspend fun mapToUiState(state: State): UiState

    protected abstract suspend fun handleEvent(event: Event)

    protected open suspend fun listen() {}

    init {
        // TODO: Fix crash
//        viewModelScope.launch {
//            listen()
//        }
    }

    private var stateFlow: StateFlow<State>? = null
    private var uiStateFlow: StateFlow<UiState>? = null

    protected val state: StateFlow<State>
        get() = stateFlow ?: run {
            stateFlow = stateFlow()
                .flowOn(Dispatchers.Default)
                .onEach {
                    Timber.d("State = $it")
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
                    initialValue = initialState(),
                )
            stateFlow!!
        }

    val uiState: StateFlow<UiState>
        get() = uiStateFlow ?: run {
            uiStateFlow = state
                .map {
                    mapToUiState(it)
                }
                .onEach {
                    Timber.d("UI state = $it")
                }
                .flowOn(Dispatchers.Default)
                .stateIn(
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